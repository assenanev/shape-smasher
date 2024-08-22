package com.xquadro.android.shapesmasher;

import com.badlogic.gdx.utils.Pool;

public class ShapePool extends Pool<Shape> {

	@Override
	protected Shape newObject() {
		Shape shape = new Shape();
		return shape;
	}

}
