package org.join.raypick;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.join.ogles.lib.Ray;
import org.join.ogles.lib.Vector3f;
import org.join.ogles.lib.Vector4f;

public class Cube {

	public static final int VERTEX_BUFFER = 0;
	public static final int TEXTURE_BUFFER = 1;

	private float one = 1.0f;

	// 立方体顶点坐标
	private float[] vertices = new float[] { -one, -one, one, one, -one, one,
			one, one, one, -one, one, one, -one, -one, -one, -one, one, -one,
			one, one, -one, one, -one, -one, -one, one, -one, -one, one, one,
			one, one, one, one, one, -one, -one, -one, -one, one, -one, -one,
			one, -one, one, -one, -one, one, one, -one, -one, one, one, -one,
			one, one, one, one, -one, one, -one, -one, -one, -one, -one, one,
			-one, one, one, -one, one, -one };

	// 立方体纹理坐标
	private float[] texCoords = new float[] { one, 0, 0, 0, 0, one, one, one,
			0, 0, 0, one, one, one, one, 0, one, one, one, 0, 0, 0, 0, one, 0,
			one, one, one, one, 0, 0, 0, 0, 0, 0, one, one, one, one, 0, one,
			0, 0, 0, 0, one, one, one };

	// 三角形描述顺序
	private byte[] indices = new byte[] { 0, 1, 3, 2, 4, 5, 7, 6, 8, 9, 11, 10,
			12, 13, 15, 14, 16, 17, 19, 18, 20, 21, 23, 22 };

	// 触碰的立方体某一面的标记（0-5）
	public int surface = -1;

	// 获得坐标的缓存对象
	public FloatBuffer getCoordinate(int coord_id) {
		switch (coord_id) {
		case VERTEX_BUFFER:
			return getDirectBuffer(vertices);
		case TEXTURE_BUFFER:
			return getDirectBuffer(texCoords);
		default:
			throw new IllegalArgumentException();
		}
	}

	// 获得三角形描述顺序
	public ByteBuffer getIndices() {
		return ByteBuffer.wrap(indices);
	}

	public FloatBuffer getDirectBuffer(float[] buffer) {
		ByteBuffer bb = ByteBuffer.allocateDirect(buffer.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer directBuffer = bb.asFloatBuffer();
		directBuffer.put(buffer);
		directBuffer.position(0);
		return directBuffer;
	}

	// 返回立方体外切圆的中心点
	public Vector3f getSphereCenter() {
		return new Vector3f(0, 0, 0);
	}

	// 返回立方体外切圆的半径（√3）
	public float getSphereRadius() {
		return 1.732051f;
	}

	private static Vector4f location = new Vector4f();

	/**
	 * 射线与模型的精确碰撞检测
	 * 
	 * @param ray
	 *            - 转换到模型空间中的射线
	 * @param trianglePosOut
	 *            - 返回的拾取后的三角形顶点位置
	 * @return 如果相交，返回true
	 */
	public boolean intersect(Ray ray, Vector3f[] trianglePosOut) {
		boolean bFound = false;
		// 存储着射线原点与三角形相交点的距离
		// 我们最后仅仅保留距离最近的那一个
		float closeDis = 0.0f;

		Vector3f v0, v1, v2;

		// 立方体6个面
		for (int i = 0; i < 6; i++) {

			// 每个面两个三角形
			for (int j = 0; j < 2; j++) {
				if (0 == j) {
					v0 = getVector3f(indices[i * 4 + j]);
					v1 = getVector3f(indices[i * 4 + j + 1]);
					v2 = getVector3f(indices[i * 4 + j + 2]);
				} else {
					// 第二个三角形时，换下顺序，不然会渲染到立方体内部
					v0 = getVector3f(indices[i * 4 + j]);
					v1 = getVector3f(indices[i * 4 + j + 2]);
					v2 = getVector3f(indices[i * 4 + j + 1]);
				}

				// 进行射线和三角行的碰撞检测
				if (ray.intersectTriangle(v0, v1, v2, location)) {
					// 如果发生了相交
					if (!bFound) {
						// 如果是初次检测到，需要存储射线原点与三角形交点的距离值
						bFound = true;
						closeDis = location.w;
						trianglePosOut[0].set(v0);
						trianglePosOut[1].set(v1);
						trianglePosOut[2].set(v2);
						surface = i;
					} else {
						// 如果之前已经检测到相交事件，则需要把新相交点与之前的相交数据相比较
						// 最终保留离射线原点更近的
						if (closeDis > location.w) {
							closeDis = location.w;
							trianglePosOut[0].set(v0);
							trianglePosOut[1].set(v1);
							trianglePosOut[2].set(v2);
							surface = i;
						}
					}
				}
			}
		}
		return bFound;
	}

	private Vector3f getVector3f(int start) {
		return new Vector3f(vertices[3 * start], vertices[3 * start + 1],
				vertices[3 * start + 2]);
	}
}
