package engine;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.stb.STBImage.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class Texture {
	
	private int width, height, texture;
	private String filepath;
	
	public Texture(String filepath) {
		this.filepath = filepath;
		init();
	}
	
	public void init() {
		stbi_set_flip_vertically_on_load(true);
		
		texture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texture);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		
		IntBuffer widthB = BufferUtils.createIntBuffer(1);
        IntBuffer heightB = BufferUtils.createIntBuffer(1);
        IntBuffer nrChannelsB = BufferUtils.createIntBuffer(1);
		
		ByteBuffer data = stbi_load(filepath, widthB, heightB, nrChannelsB, 0);
		
		width = widthB.get(0);
		height = heightB.get(0);
		if (nrChannelsB.get(0) == 3) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, data);
        } else if (nrChannelsB.get(0) == 4) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height,0, GL_RGBA, GL_UNSIGNED_BYTE, data);
        }
		
		stbi_image_free(data);
	}
	
	public int getTexture() {
		return texture;
	}
}
