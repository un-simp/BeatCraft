package com.beatcraft.render.effect;

import com.beatcraft.BeatCraft;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.function.TriConsumer;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.vivecraft.client_vr.ClientDataHolderVR;

import java.io.IOException;
import java.util.ArrayList;

public class Bloomfog {

    public static class BloomfogTex extends AbstractTexture {

        private final SimpleFramebuffer buffer;
        protected BloomfogTex(SimpleFramebuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public int getGlId() {
            return buffer.getColorAttachment();
        }

        @Override
        public void load(ResourceManager manager) throws IOException {

        }
    }

    public boolean overrideBuffer = false;
    public Framebuffer overrideFramebuffer = null;

    public SimpleFramebuffer framebuffer;
    private final ArrayList<TriConsumer<BufferBuilder, Vector3f, Quaternionf>> renderCalls = new ArrayList<>();
    private final Identifier textureId = Identifier.of(BeatCraft.MOD_ID, "bloomfog/main");
    private final BloomfogTex tex;

    private final SimpleFramebuffer[] pingPongBuffers = new SimpleFramebuffer[2];
    private final Identifier[] pingPongTexIds = new Identifier[]{
            Identifier.of(BeatCraft.MOD_ID, "bloomfog/ping_pong_0"),
            Identifier.of(BeatCraft.MOD_ID, "bloomfog/ping_pong_1")
    };
    private final BloomfogTex[] pingPongTextures = new BloomfogTex[2];

    private final SimpleFramebuffer blurredBuffer;
    private final Identifier blurredTexId = Identifier.of(BeatCraft.MOD_ID, "bloomfog/blurred");
    private BloomfogTex blurredTex;

    private final ShaderProgram blurShaderUp;
    private final ShaderProgram blurShaderDown;
    public static ShaderProgram bloomfog_solid_shader;

    public static ShaderProgram bloomfogPositionColor;

    //private final Uniform vTexSize;
    //private final Uniform hTexSize;

    private Bloomfog() {

        try {
            blurShaderUp = new ShaderProgram(MinecraftClient.getInstance().getResourceManager(), "bloomfog_blur_up", VertexFormats.POSITION_TEXTURE);
            blurShaderDown = new ShaderProgram(MinecraftClient.getInstance().getResourceManager(), "bloomfog_blur_down", VertexFormats.POSITION_TEXTURE);
            bloomfogPositionColor = new ShaderProgram(MinecraftClient.getInstance().getResourceManager(), "col_bloomfog", VertexFormats.POSITION_COLOR);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        framebuffer = new SimpleFramebuffer(1920, 1080, true, true);
        pingPongBuffers[0] = new SimpleFramebuffer(1920, 1080, true, true);
        pingPongBuffers[1] = new SimpleFramebuffer(1920, 1080, true, true);
        blurredBuffer = new SimpleFramebuffer(1920, 1080, true, true);

        tex = new BloomfogTex(framebuffer);
        pingPongTextures[0] = new BloomfogTex(pingPongBuffers[0]);
        pingPongTextures[1] = new BloomfogTex(pingPongBuffers[1]);
        blurredTex = new BloomfogTex(blurredBuffer);

        MinecraftClient.getInstance().getTextureManager().registerTexture(textureId, tex);
        MinecraftClient.getInstance().getTextureManager().registerTexture(pingPongTexIds[0], pingPongTextures[0]);
        MinecraftClient.getInstance().getTextureManager().registerTexture(pingPongTexIds[1], pingPongTextures[1]);
        MinecraftClient.getInstance().getTextureManager().registerTexture(blurredTexId, blurredTex);

    }


    private static Bloomfog INSTANCE = null;
    public static Bloomfog create() {
        if (INSTANCE == null) {
            INSTANCE = new Bloomfog();
        }
        return INSTANCE;
    }

    public void resize(int width, int height) {
        framebuffer.resize(width, height, true);
        pingPongBuffers[0].resize(width, height, true);
        pingPongBuffers[1].resize(width, height, true);
        blurredBuffer.resize(width, height, true);
    }

    public void unload() {
        MinecraftClient.getInstance().getTextureManager().destroyTexture(textureId);
        framebuffer.delete();
    }

    public int getTexture() {
        return framebuffer.getColorAttachment();
    }

    public void record(TriConsumer<BufferBuilder, Vector3f, Quaternionf> call) {
        renderCalls.add(call);
    }

    private int[] lastSize = new int[]{1, 1};
    public void render(float tickDelta) {

        if (ClientDataHolderVR.getInstance().vr != null && ClientDataHolderVR.getInstance().vr.isActive()) {
            renderCalls.clear();
            return;
        }

        framebuffer.setClearColor(0, 0, 0, 0);
        framebuffer.clear(true);

        MinecraftClient client = MinecraftClient.getInstance();
        var window = client.getWindow();

        if (window.getWidth() != lastSize[0] || window.getHeight() != lastSize[1]) {
            lastSize = new int[]{Math.max(1, window.getWidth()), Math.max(1, window.getHeight())};
            resize(Math.max(1, window.getWidth()), Math.max(1, window.getHeight()));
        }

        float aspectRatio = (float) window.getWidth() / (float) window.getHeight();

        float t = RenderSystem.getProjectionMatrix().m11();
        float fov = ((float) Math.atan(1.0f / t)) * 2.0f;

        overrideBuffer = true;
        overrideFramebuffer = framebuffer;
        framebuffer.beginWrite(true);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        Vector3f cameraPos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos().toVector3f();

        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        RenderSystem.lineWidth(window.getWidth()/225f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();

        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();

        Vector3f up = camera.getVerticalPlane();
        Vector3f left = camera.getDiagonalPlane();

        var invCameraRotation = camera.getRotation().conjugate(new Quaternionf());


        for (var call : renderCalls) {
            call.accept(buffer, cameraPos, invCameraRotation);
        }



        float quadHeight = (float) Math.tan(fov / 2.0f);
        float quadWidth = quadHeight * aspectRatio;

        renderCalls.clear();

        var buff = buffer.endNullable();
        if (buff != null) {
            BufferRenderer.drawWithGlobalProgram(buff);
        }

        framebuffer.endWrite();
        overrideBuffer = false;
        overrideFramebuffer = null;


        var oldProjMat = RenderSystem.getProjectionMatrix();
        var oldSort = RenderSystem.getVertexSorting();

        Matrix4f orthoMatrix = new Matrix4f();
        orthoMatrix.ortho(-0.5f, 0.5f, -0.5f, 0.5f, 1, -1);

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);

        applyBlur(quadWidth, quadHeight);

        MinecraftClient.getInstance().getFramebuffer().beginWrite(true);

        RenderSystem.depthMask(false);

        buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);



        buffer.vertex(-quadWidth / 2, -quadHeight / 2, -0.5f).texture(0.0f, 0.0f); // Top-left
        buffer.vertex( quadWidth / 2, -quadHeight / 2, -0.5f).texture(1.0f, 0.0f); // Top-right
        buffer.vertex( quadWidth / 2,  quadHeight / 2, -0.5f).texture(1.0f, 1.0f); // Bottom-right
        buffer.vertex(-quadWidth / 2,  quadHeight / 2, -0.5f).texture(0.0f, 1.0f); // Bottom-left


        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, blurredTexId);
        RenderSystem.enableBlend();

        //GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        //GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.setProjectionMatrix(oldProjMat, oldSort);

        RenderSystem.enableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(true);

        //RenderSystem.setShader(() -> bloomfogPositionColor);
        //blurredBuffer.beginRead();
        //bloomfogPositionColor.addSampler("Sampler0", blurredBuffer.getColorAttachment());
        //blurredBuffer.endRead();

        //try {
        //    GameRenderer.getRenderTypeSolidProgram().addSampler("Bloomfog", blurredBuffer);
        //} catch (Exception e) {
        //    throw new RuntimeException(e);
        //}
        //GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, prevMinFilter);
        //GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, prevMagFilter);

    }

    public void loadTex() {
        RenderSystem.setShaderTexture(0, blurredBuffer.getColorAttachment());
    }

    private void applyBlur(float width, float height) {
        // blur input to ping-pong
        // blur ping-pong back and forth a couple times
        // blur ping-pong to output


        //GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        //GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        //Vector3f cameraPos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos().toVector3f();
        //Quaternionf cameraRot = MinecraftClient.getInstance().gameRenderer.getCamera().getRotation().conjugate(new Quaternionf());

        applyBlurPass(framebuffer, pingPongBuffers[0], width, height, false);

        int passes = 3;

        for (int i = 0; i < passes; i++) {
            applyBlurPass(pingPongBuffers[0], pingPongBuffers[1], width, height, false);
            applyBlurPass(pingPongBuffers[1], pingPongBuffers[0], width, height, false);
        }

        for (int i = 0; i < passes; i++) {
            applyBlurPass(pingPongBuffers[0], pingPongBuffers[1], width, height, true);
            applyBlurPass(pingPongBuffers[1], pingPongBuffers[0], width, height, true);
        }

        applyBlurPass(pingPongBuffers[0], blurredBuffer, width, height, true);

    }

    private void applyBlurPass(Framebuffer in, Framebuffer out, float width, float height, boolean upPass) {

        out.setClearColor(0, 0, 0, 0);
        out.clear(true);
        out.beginWrite(true);
        overrideBuffer = true;
        overrideFramebuffer = out;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        float w = (float) MinecraftClient.getInstance().getWindow().getWidth();
        float h = (float) MinecraftClient.getInstance().getWindow().getHeight();
        float a = (w/h) * 0.003f;
        float a2 = 0.004f;

        RenderSystem.setShaderTexture(0, in.getColorAttachment());
        RenderSystem.setShader(upPass ? (() -> blurShaderUp) : (() -> blurShaderDown));
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        buffer.vertex(new Vector3f(-width/2,  height/2, -0.5f)).texture(0, 0).color(a2, a, 0, 0);
        buffer.vertex(new Vector3f( width/2,  height/2, -0.5f)).texture(1, 0).color(a2, a, 0, 0);
        buffer.vertex(new Vector3f( width/2, -height/2, -0.5f)).texture(1, 1).color(a2, a, 0, 0);
        buffer.vertex(new Vector3f(-width/2, -height/2, -0.5f)).texture(0, 1).color(a2, a, 0, 0);

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        out.endWrite();

        overrideBuffer = false;
        overrideFramebuffer = null;

    }

}