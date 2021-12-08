package engine;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetKey;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
	private static float lastX = 400;
	private static float lastY = 300;
	private static float yaw = -90.0f;
	private static float pitch;
	private static float Zoom = 45.0f;
	private static boolean firstMouse = true;
	private static Vector3f cameraPos = new Vector3f(0.0f, 0.0f, 3.0f);
	private static Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
	private static Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
	private static Vector3f direction = new Vector3f(0.0f, 0.0f, 0.0f);
	
	public Camera() {
		
	}
	
	public Matrix4f getViewMatrix() {
		Matrix4f view = new Matrix4f();
		view.lookAt(cameraPos, new Vector3f(cameraFront.x + cameraPos.x, cameraFront.y + cameraPos.y, cameraFront.z + cameraPos.z), cameraUp);
		return view;
	}
	public Matrix4f getProjectionMatrix() {
		Matrix4f projection = new Matrix4f();
		projection.identity();
		projection.perspective((float)Math.toRadians(Zoom), 800.0f / 600.0f, 0.1f, 100.0f);
		return projection;
	}
	
	public void processInput(long window, float deltaTime) {
		float cameraSpeed = 2.5f * deltaTime;
		
		if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
			Vector3f product = new Vector3f();
			cameraFront.mul(cameraSpeed, product);
			cameraPos.add(product);
		}
		if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
			Vector3f product = new Vector3f();
			cameraFront.mul(cameraSpeed, product);
			cameraPos.sub(product);
		}
		if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
			Vector3f crossProduct = new Vector3f();
			cameraFront.cross(cameraUp, crossProduct);
			cameraPos.sub(crossProduct.normalize().mul(cameraSpeed));
		}
		if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
			Vector3f crossProduct = new Vector3f();
			cameraFront.cross(cameraUp, crossProduct);
			cameraPos.add(crossProduct.normalize().mul(cameraSpeed));
		}
	}
	
	public static void mouseCallback(long window, double xpos, double ypos) {
		if (firstMouse) {
			lastX = (float) xpos;
			lastY = (float) ypos;
			firstMouse = false;
		}
		
		float xoffset = (float) (xpos - lastX);
		float yoffset = (float) (lastY - ypos); // reversed: y ranges bottom to top
		lastX = (float) xpos;
		lastY = (float) ypos;
		float sensitivity = 0.1f;
		xoffset *= sensitivity;
		yoffset *= sensitivity;

		yaw += xoffset;
		pitch += yoffset;
		
		if (pitch > 89.0f) {
			pitch = 89.0f;
		}
		if (pitch < -89.0f) {
			pitch = -89.0f;
		}
		
		direction.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
		direction.y = (float) (Math.sin(Math.toRadians(pitch)));
		direction.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
		direction.normalize(cameraFront);
	}
	
	public static void scrollCallback(long window, double xoffset, double  yoffset) {
		Zoom -= (float)yoffset;
		if (Zoom <= 1.0f) {
			Zoom = 1.0f;
		}
		if (Zoom >= 45.0f) {
			Zoom = 45.0f;
		}
	}
}
