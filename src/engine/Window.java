package engine;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.GL30.*;

public class Window {
	
	private long window;
	private int VAO, VBO, EBO;
	private float zOffset = -3.0f;
	private Shader shaderProgram;
	private Texture container, face;
	private Vector3f cubePositions[] = {
			new Vector3f( 0.0f, 0.0f, 0.0f),
			new Vector3f( 2.0f, 5.0f, -15.0f),
			new Vector3f(-1.5f, -2.2f, -2.5f),
			new Vector3f(-3.8f, -2.0f, -12.3f),
			new Vector3f( 2.4f, -0.4f, -3.5f),
			new Vector3f(-1.7f, 3.0f, -7.5f),
			new Vector3f( 1.3f, -2.0f, -2.5f),
			new Vector3f( 1.5f, 2.0f, -2.5f),
			new Vector3f( 1.5f, 0.2f, -1.5f),
			new Vector3f(-1.3f, 1.0f, -1.5f)
	};
	
	public Window() {
		
	}
	
	public void run() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		init();
		loop();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	
	private void init() {
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(800, 600, "Learn OpenGL", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);

		GL.createCapabilities();
		glEnable(GL_DEPTH_TEST);
	        
		float vertices[] = {
		        -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
		         0.5f, -0.5f, -0.5f,  1.0f, 0.0f,
		         0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
		         0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
		        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
		        -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,

		        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
		         0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
		         0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
		         0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
		        -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,
		        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,

		        -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
		        -0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
		        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
		        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
		        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
		        -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

		         0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
		         0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
		         0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
		         0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
		         0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
		         0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

		        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
		         0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
		         0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
		         0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
		        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
		        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,

		        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
		         0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
		         0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
		         0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
		        -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,
		        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f
		};
  
		VAO = glGenVertexArrays();
		VBO = glGenBuffers();
		EBO = glGenBuffers();
	    
		glBindVertexArray(VAO);
	    
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
//        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, 0); 
        glBindVertexArray(0);
        
		shaderProgram = new Shader(window);	
		shaderProgram.use();
		shaderProgram.setInt("texture1", 0);
		shaderProgram.setInt("texture2", 1);
		
		container = new Texture("textures/container.jpg");
		face = new Texture("textures/awesomeface.png");
	}
	
	private void loop() {
    
		while ( !glfwWindowShouldClose(window) ) {
			
			processInput(window);
			
			glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			glBindVertexArray(VAO);
			for (int i = 0; i < 10; i++) {
				Matrix4f model = new Matrix4f();
				model.identity();
				model.translate(cubePositions[i], model);
				model.rotate((float)(Math.toRadians(glfwGetTime() * (i + 1) * 20.0f)), new Vector3f(1.0f, 0.3f, 0.5f).normalize(), model);
				shaderProgram.setMatrix4fv("model", model);
				glDrawArrays(GL_TRIANGLES, 0, 36);
			}
			
			Matrix4f view = new Matrix4f();
			view.identity();
			view.translate(new Vector3f(0.0f, 0.0f, zOffset), view);
			Matrix4f projection = new Matrix4f();
			projection.identity();
			projection.perspective((float)Math.toRadians(45.0f), 800.0f / 600.0f, 0.1f, 100.0f);
			
			shaderProgram.setMatrix4fv("view", view);
			shaderProgram.setMatrix4fv("projection", projection);
			
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, container.getTexture());
			glActiveTexture(GL_TEXTURE1);
			glBindTexture(GL_TEXTURE_2D, face.getTexture());	
			
			glfwSwapBuffers(window);
			glfwPollEvents();
			
		}
		
	}
	
	private void processInput(long window) {
		if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
			glfwSetWindowShouldClose(window, true);
		}
		if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
			zOffset = zOffset + 0.05f;
		}
		if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
			zOffset = zOffset - 0.05f;
		}
	}
}
