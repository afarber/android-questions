package de.afarber.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

class GameBoard extends View {
	private int nSquares, colorA, colorB;

	private Paint paint;
	private int squareDim;

	public GameBoard(Context context, int nSquares, int colorA, int colorB) {
		super(context);
		this.nSquares = nSquares;
		this.colorA = colorA;
		this.colorB = colorB;
		paint = new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		for (int row = 0; row < nSquares; row++) {
			paint.setColor(((row & 1) == 0) ? colorA : colorB);
			for (int col = 0; col < nSquares; col++) {
				int a = col * squareDim;
				int b = row * squareDim;
				canvas.drawRect(a, b, a + squareDim, b + squareDim, paint);
				paint.setColor((paint.getColor() == colorA) ? colorB : colorA);
			}
		}
	}
	@Override
	protected void onMeasure(int widthMeasuredSpec, int heightMeasuredSpec) {
		int width = MeasureSpec.getSize(widthMeasuredSpec);
		int height = MeasureSpec.getSize(heightMeasuredSpec);
		int d = (width == 0) ? height : (height == 0) ? width
				: (width < height) ? width : height;
		setMeasuredDimension(d, d);
		squareDim = width / nSquares;
	}
}

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        GameBoard gb = new GameBoard(this, 8, Color.GRAY, Color.WHITE);
        setContentView(gb);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
