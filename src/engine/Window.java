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
	private int VAO, lightVAO, VBO, EBO;
	private Shader defaultShader;
	private Shader lightShader;
	private Camera camera;
	private Texture container, face;
	private Vector3f lightPos = new Vector3f(1.2f, 1.0f, 2.0f);
	
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
		
		// Grab cursor
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		glfwSetCursorPosCallback(window, Camera::mouseCallback);
		glfwSetScrollCallback(window, Camera::scrollCallback);

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
		lightVAO = glGenVertexArrays();
		VBO = glGenBuffers();
		EBO = glGenBuffers();
	    
		// General VAO *************
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
        
        // Light VAO *************
        glBindVertexArray(lightVAO);
        
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);	
        
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0); 
        
        lightShader = new Shader(window, "shaders/light.glsl");	
        defaultShader = new Shader(window, "shaders/default.glsl");	
        
		camera = new Camera();
	}
	
	private void loop() {
    
		float deltaTime = 0.0f;
		float currentFrame = 0.0f;
		float lastFrame = 0.0f;
		
		while ( !glfwWindowShouldClose(window) ) {
			
			processInput(window, deltaTime);
			camera.processInput(window, deltaTime);
			
			glClearColor(0.1f, 0.1f, 0.1f, 1.0f); // Nice green: 0.2f, 0.3f, 0.3f, 1.0f
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			Matrix4f model = new Matrix4f();
			model.identity();

			defaultShader.use();
			defaultShader.setVec3("objectColor", 1.0f, 0.5f, 0.31f);
			defaultShader.setVec3("lightColor", 1.0f, 1.0f, 1.0f);
			defaultShader.setMatrix4fv("model", model);
			defaultShader.setMatrix4fv("view", camera.getViewMatrix());
			defaultShader.setMatrix4fv("projection", camera.getProjectionMatrix());
			glBindVertexArray(VAO);
			glDrawArrays(GL_TRIANGLES, 0, 36);	
			
			model.identity();
			model.translate(lightPos, model);
			model.scale(new Vector3f(0.2f), model);
			
			lightShader.use();
			lightShader.setMatrix4fv("model", model);
			lightShader.setMatrix4fv("view", camera.getViewMatrix());
			lightShader.setMatrix4fv("projection", camera.getProjectionMatrix());
			glBindVertexArray(lightVAO);
			glDrawArrays(GL_TRIANGLES, 0, 36);
			
			glfwSwapBuffers(window);
			glfwPollEvents();
			
			currentFrame = (float) glfwGetTime();
			deltaTime = currentFrame - lastFrame;
			lastFrame = currentFrame;
		}	
	}
	
	private void processInput(long window, float deltaTime) {

		if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
			glfwSetWindowShouldClose(window, true);
		}
		
	}
}
