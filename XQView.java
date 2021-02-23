package com.XQ.xq1_0;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class XQView extends View {
	public XQView view1 = null;
	private Paint linePaint = null;
	private Paint redPaint = null;// red红
	private Paint whitePaint = null;// 白边
	private Paint blackPaint = null;// 画棋子_黑
	private Paint fugaiPaint = null;
	private int startX = 0;// 定义初始位置,左上起始绘制点
	private int startY = 0;
	private int buchang = 0;// 棋格大小

	// 储存当前红黑子的位置，其中行代表棋子的序号，列代表x，y坐标
	private int[][] RedLoc = new int[17][2];
	private int[][] BlackLoc = new int[17][2];
	// 储存被选中的棋子的编号
	private int[][] Loc = new int[17][2];// 储存行棋过程便于悔棋与落子覆盖与点击错误位置时的重置
	private int Slt = 16;
	private int hui = 16;// 储存

	// 储存当前是哪一方行走，true是红方，false是黑方,规定红方先行
	private boolean MoveSide = true;
	private boolean flag = false;// 判断开始
	private boolean fugai = false;// 判断是否覆盖
	private boolean result = false;
	private boolean jz = false;// 将军
	Canvas canvas;

	public XQView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 画笔
		fugaiPaint = new Paint();
		fugaiPaint.setColor(Color.GREEN);
		blackPaint = new Paint();
		blackPaint.setColor(Color.BLACK);
		redPaint = new Paint();
		redPaint.setColor(Color.RED);
		whitePaint = new Paint();
		whitePaint.setColor(Color.WHITE);
		linePaint = new Paint();
		// F
	}

	// 储存棋子初始位置
	public void weizhi() {
		// 初始化位置信息——————坐标表示,记录棋子位置
		RedLoc[14][0] = 1;
		RedLoc[14][1] = 7;
		RedLoc[15][0] = 7;
		RedLoc[15][1] = 7;
		BlackLoc[14][0] = 1;
		BlackLoc[14][1] = 2;
		BlackLoc[15][0] = 7;
		BlackLoc[15][1] = 2;

		for (int i = 0; i <= 8; i++) {
			RedLoc[i][0] = i;// 编号0~8————0显示X数
			RedLoc[i][1] = 9;// 第九行棋子的位置__1显示Y数
			BlackLoc[i][0] = i;
			BlackLoc[i][1] = 0;// 第一行棋子的位置
		}
		// 兵的位置
		for (int i = 9; i <= 13; i++) {
			RedLoc[i][0] = (i - 9) * 2;
			RedLoc[i][1] = 6;
			BlackLoc[i][0] = (i - 9) * 2;// 显示X数
			BlackLoc[i][1] = 3;
		}// F
	}

	// 触摸事件处理方法
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 有效触摸范围
		int X = (int) event.getX();
		int Y = (int) event.getY();
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// 未开始
			if (!flag) {
				if ((X > startX) && (X < startX + buchang * 4)
						&& (Y > startY + buchang * 10)
						&& (Y < startY + buchang * 11)) {
					weizhi();// 每次开始初始化棋子位置
					flag = true;
				}
			}// F
				// 开始
			else {
				// 重新开始
				if ((X > startX) && (X < startX + buchang * 4)
						&& (Y > startY + buchang * 10)
						&& (Y < startY + buchang * 11)) {
					// 初始条件
					flag = false;
					Slt = 16;
					MoveSide = true;
					result = false;
					fugai = false;
				}
				// F
				// 悔棋
				if ((X > startX + buchang * 4) && (X < startX + buchang * 8)
						&& (Y > startY + buchang * 10)
						&& (Y < startY + buchang * 11) && hui != 16) {
					if (fugai) {
						hui = Slt = 16;
						fugai = false;
					} else {
						// 红方悔棋
						if (!MoveSide) {
							RedLoc[hui][0] = Loc[hui][0];
							RedLoc[hui][1] = Loc[hui][1];
							hui = Slt = 16;
							fugai = false;
							MoveSide = true;
						} // F
							// 黑方悔棋
						else if (MoveSide) {
							BlackLoc[hui][0] = Loc[hui][0];
							BlackLoc[hui][1] = Loc[hui][1];
							hui = Slt = 16;
							fugai = false;
							MoveSide = false;
						}// F
					}
				}
				// 红方
				if (MoveSide && flag) {
					// 第一次点击
					if (!fugai) {
						for (int i = 0; i <= 15; i++) {
							if ((X > startX + RedLoc[i][0] * buchang - buchang
									/ 2)
									&& (X < startX + RedLoc[i][0] * buchang
											+ buchang / 2)
									&& (Y > startY + RedLoc[i][1] * buchang
											- buchang / 2)
									&& (Y < startY + RedLoc[i][1] * buchang
											+ buchang / 2)) {
								hui = Slt = i;
								fugai = true;
								Loc[Slt][0] = RedLoc[Slt][0];
								Loc[Slt][1] = RedLoc[Slt][1];
								break;
							}
						}
					}
					// F
					// 第二次点击
					else {
						// 不同棋子的走棋
						for (int i = 0; i <= 8; i++) {
							for (int j = 0; j <= 9; j++) {
								if ((X > startX + i * buchang - buchang / 2)
										&& (X < startX + i * buchang + buchang
												/ 2)
										&& (Y > startY + j * buchang - buchang
												/ 2)
										&& (Y < startY + j * buchang + buchang
												/ 2)) {
									// 判断点击是否有效
									// 车
									if (Slt == 0 || Slt == 8) {
										rche(i, j);
									}
									// F
									// 马
									else if (Slt == 1 || Slt == 7) {
										rma(i, j);
									}
									// F
									// 炮
									else if (Slt == 14 || Slt == 15) {
										rpao(i, j);
									}
									// F
									// 帅
									else if (Slt == 4) {
										rshuai(i, j);
									}
									// F
									// 相
									else if (Slt == 2 || Slt == 6) {
										rxiang(i, j);
									}
									// F
									// 仕
									else if (Slt == 3 || Slt == 5) {
										rshi(i, j);
									}
									// F
									// 兵
									else if (Slt == 9 || Slt == 10 || Slt == 11
											|| Slt == 12 || Slt == 13) {
										rzu(i, j);
									}
									// F
									fugai = false;
									if (result) {
										MoveSide = false;
										result = false;
									} else {
										Slt = 16;
									}
									break;
								}
							}
						}// F
					}
					// F
				}
				// F
				// 黑方
				else if (!MoveSide && flag) {
					// 第一次点击
					if (!fugai) {
						for (int i = 0; i <= 15; i++) {
							if ((X > startX + BlackLoc[i][0] * buchang
									- buchang / 2)
									&& (X < startX + BlackLoc[i][0] * buchang
											+ buchang / 2)
									&& (Y > startY + BlackLoc[i][1] * buchang
											- buchang / 2)
									&& (Y < startY + BlackLoc[i][1] * buchang
											+ buchang / 2)) {
								hui = Slt = i;
								fugai = true;
								Loc[Slt][0] = BlackLoc[Slt][0];
								Loc[Slt][1] = BlackLoc[Slt][1];
								break;
							}
						}
					}
					// F
					// 第二次点击
					else {
						// 不同棋子的走棋
						for (int i = 0; i <= 8; i++) {
							for (int j = 0; j <= 9; j++) {
								if ((X > startX + i * buchang - buchang / 2)
										&& (X < startX + i * buchang + buchang
												/ 2)
										&& (Y > startY + j * buchang - buchang
												/ 2)
										&& (Y < startY + j * buchang + buchang
												/ 2)) {
									// 判断点击是否有效
									// 车
									if (Slt == 0 || Slt == 8) {
										rcheb(i, j);
									}
									// F
									// 马
									else if (Slt == 1 || Slt == 7) {
										rmab(i, j);
									}
									// F
									// 炮
									else if (Slt == 14 || Slt == 15) {
										rpaob(i, j);
									}
									// F
									// 帅
									else if (Slt == 4) {
										jiang(i, j);
									}
									// F
									// 相
									else if (Slt == 2 || Slt == 6) {
										rxiangb(i, j);
									}
									// F
									// 仕
									else if (Slt == 3 || Slt == 5) {
										rshib(i, j);
									}
									// F
									// 兵
									else if (Slt == 9 || Slt == 10 || Slt == 11
											|| Slt == 12 || Slt == 13) {
										rzub(i, j);
									}
									// F
									fugai = false;
									if (result) {
										MoveSide = true;
										result = false;
									} else {
										Slt = 16;
									}
									break;
								}
							}
						}// F
					}
					// F
				}
				// F
			}// F
		}
		invalidate();
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 屏幕适配
		buchang = getMeasuredWidth() / 9;
		startX = buchang / 2;
		startY = 2 * buchang;
		// F
		// 初始屏幕
		canvas.drawColor(Color.argb(0xBF, 99, 66, 33));
		linePaint.setColor(Color.argb(0xFF, 0xdf, 0xbb, 0x9a));
		canvas.drawRect(startX - buchang * 50 / 100, startY - buchang * 53
				/ 100, startX + buchang * 8 + buchang * 50 / 100, startY
				+ buchang * 9 + buchang * 53 / 100, linePaint);// 边框
		redPaint.setTextSize(buchang * 2 / 3);
		blackPaint.setTextSize(buchang * 2 / 3);
		linePaint.setStrokeWidth(4);
		linePaint.setColor(Color.argb(0xBF, 99, 66, 33));
		onqige(canvas);
		// 开始区域
		canvas.drawRect(startX, startY + buchang * 10, startX + buchang * 4,
				startY + buchang * 11, redPaint);
		// F
		// 悔棋区域
		canvas.drawRect(startX + buchang * 4, startY + buchang * 10, startX
				+ buchang * 8, startY + buchang * 11, blackPaint);
		// F
		// 提示区域
		canvas.drawRect(startX, startY + buchang * 12, startX + buchang * 8,
				startY + buchang * 13, whitePaint);
		// F
		redPaint.setTextSize(buchang * 53 / 100);
		blackPaint.setTextSize(buchang * 53 / 100);
		// F
		// 游戏开始
		if (flag) {
			for (int i = 0; i <= 15; i++) {
				//
				if (i != Slt) {
					QZ(canvas, i);
				}// F
				else {
					if (fugai) {
						dijifugai(canvas, Slt);
					} //
					else {
						QZ(canvas, Slt);
						Slt = 16;
					}
				}// F
			}
		}
	}

	private void onqige(Canvas canvas) {
		// 棋盘线绘制开始
		for (int i = 0; i <= 8; i++) {
			int x = startX + buchang * i;
			canvas.drawLine(x, startY, x, startY + buchang * 9, linePaint);
		}
		for (int j = 0; j <= 9; j++) {
			int y = startY + buchang * j;
			canvas.drawLine(startX, y, startX + buchang * 8, y, linePaint);
		}
		canvas.drawLine(startX - buchang / 10, startY - buchang / 10, startX
				+ buchang * 8 + buchang / 10, startY - buchang / 10, linePaint);
		canvas.drawLine(startX - buchang / 10, startY + buchang * 9 + buchang
				/ 10, startX + buchang * 8 + buchang / 10, startY + buchang * 9
				+ buchang / 10, linePaint);
		canvas.drawLine(startX - buchang / 10, startY - buchang / 10, startX
				- buchang / 10, startY + buchang * 9 + buchang / 10, linePaint);
		canvas.drawLine(startX + buchang * 8 + buchang / 10, startY - buchang
				/ 10, startX + buchang * 8 + buchang / 10, startY + buchang * 9
				+ buchang / 10, linePaint);
		// 棋盘线绘制结束
		// 楚河漢界开始
		linePaint.setColor(Color.argb(0xFF, 0xdf, 0xbb, 0x9a));
		canvas.drawRect(startX + 2, startY + buchang * 4 + 2, startX + buchang
				* 8 - 2, startY + buchang * 5 - 2, linePaint);
		linePaint.setColor(Color.argb(0xBF, 99, 66, 33));
		// 字符位置baseline使用getTextBounds
		float top = startY + buchang * 4;
		float bottom = startY + buchang * 5;
		Rect txtRec = new Rect();
		blackPaint.getTextBounds("楚", 0, "楚".length(), txtRec);
		float Hegt = txtRec.height();
		canvas.drawText("楚", startX + buchang * 5 / 10, (top + bottom) / 2
				+ Hegt / 2, blackPaint);
		blackPaint.getTextBounds("河", 0, "河".length(), txtRec);
		Hegt = txtRec.height();
		canvas.drawText("河", startX + buchang * 25 / 10, (top + bottom) / 2
				+ Hegt / 2, blackPaint);
		redPaint.getTextBounds("汉", 0, "汉".length(), txtRec);
		Hegt = txtRec.height();
		canvas.drawText("汉", startX + buchang * 45 / 10, (top + bottom) / 2
				+ Hegt / 2, redPaint);
		redPaint.getTextBounds("界", 0, "界".length(), txtRec);
		Hegt = txtRec.height();
		canvas.drawText("界", startX + buchang * 65 / 10, (top + bottom) / 2
				+ Hegt / 2, redPaint);
		// 楚河漢界结束

		// 特殊的线，将帅，炮，卒开始
		canvas.drawLine(startX + buchang * 3, startY, startX + buchang * 5,
				startY + buchang * 2, linePaint);
		canvas.drawLine(startX + buchang * 5, startY, startX + buchang * 3,
				startY + buchang * 2, linePaint);
		canvas.drawLine(startX + buchang * 3, startY + buchang * 9, startX
				+ buchang * 5, startY + buchang * 7, linePaint);
		canvas.drawLine(startX + buchang * 5, startY + buchang * 9, startX
				+ buchang * 3, startY + buchang * 7, linePaint);

		// 特殊的线，将帅，炮，卒结束
		// 特殊节点开始
		int a = buchang * 3 / 10;
		int b = buchang / 10;
		for (int i = 0; i <= 8; i++) {
			int x = startX + buchang * i;
			for (int j = 0; j <= 9; j++) {
				int y = startY + buchang * j;
				if (((i == 1 || i == 7) && (j == 2 || j == 7))
						|| ((i == 2 || i == 4 || i == 6) && (j == 3 || j == 6))) {

					canvas.drawLine(x + b, y - b, x + a, y - b, linePaint);
					canvas.drawLine(x + b, y - b, x + b, y - a, linePaint);// 右上

					canvas.drawLine(x + b, y + b, x + a, y + b, linePaint);
					canvas.drawLine(x + b, y + b, x + b, y + a, linePaint);// 右下

					canvas.drawLine(x - b, y - b, x - a, y - b, linePaint);
					canvas.drawLine(x - b, y - b, x - b, y - a, linePaint);// 左上

					canvas.drawLine(x - b, y + b, x - a, y + b, linePaint);
					canvas.drawLine(x - b, y + b, x - b, y + a, linePaint);// 左下

				} else if (i == 0 && (j == 3 || j == 6)) {
					canvas.drawLine(x + b, y - b, x + a, y - b, linePaint);
					canvas.drawLine(x + b, y - b, x + b, y - a, linePaint);// 右上

					canvas.drawLine(x + b, y + b, x + a, y + b, linePaint);
					canvas.drawLine(x + b, y + b, x + b, y + a, linePaint);// 右下
				} else if (i == 8 && (j == 3 || j == 6)) {
					canvas.drawLine(x - b, y - b, x - a, y - b, linePaint);
					canvas.drawLine(x - b, y - b, x - b, y - a, linePaint);// 左上

					canvas.drawLine(x - b, y + b, x - a, y + b, linePaint);
					canvas.drawLine(x - b, y + b, x - b, y + a, linePaint);// 左下
				}

			}
		}
	}

	public void QZ(Canvas canvas, int i) {
		switch (i) {
		case 0:
			che(canvas, i);
			break;
		case 8:
			che(canvas, i);
			break;
		case 1:
			ma(canvas, i);
			break;
		case 7:
			ma(canvas, i);
			break;
		case 14:
			pao(canvas, i);
			break;
		case 15:
			pao(canvas, i);
			break;
		case 2:
			xiang(canvas, i);
			break;
		case 6:
			xiang(canvas, i);
			break;
		case 3:
			shi(canvas, i);
			break;
		case 5:
			shi(canvas, i);
			break;
		case 4:
			wang(canvas, i);
			break;
		case 9:
			zu(canvas, i);
			break;
		case 10:
			zu(canvas, i);
			break;
		case 11:
			zu(canvas, i);
			break;
		case 12:
			zu(canvas, i);
			break;
		case 13:
			zu(canvas, i);
			break;
		default:
			break;

		}
	}

	public void fugai(Canvas canvas, int m, int n, int x, int y) {
		canvas.drawCircle(m, n, buchang * 47 / 100, blackPaint);
		canvas.drawCircle(m, n, buchang * 41 / 100, whitePaint);
		canvas.drawCircle(m, n, buchang * 37 / 100, blackPaint);
		canvas.drawCircle(m, n, buchang * 33 / 100, whitePaint);
		canvas.drawCircle(x, y, buchang * 47 / 100, redPaint);
		canvas.drawCircle(x, y, buchang * 41 / 100, whitePaint);
		canvas.drawCircle(x, y, buchang * 37 / 100, redPaint);
		canvas.drawCircle(x, y, buchang * 33 / 100, whitePaint);
	}

	public void dijifugai(Canvas canvas, int i) {
		int x = startX + RedLoc[i][0] * buchang;
		int y = startY + RedLoc[i][1] * buchang;
		int m = startX + BlackLoc[i][0] * buchang;
		int n = startY + BlackLoc[i][1] * buchang;
		fugaiPaint.setTextSize(buchang * 53 / 100);
		if (MoveSide) {
			canvas.drawCircle(x, y, buchang * 47 / 100, fugaiPaint);
			canvas.drawCircle(x, y, buchang * 41 / 100, whitePaint);
			canvas.drawCircle(x, y, buchang * 37 / 100, fugaiPaint);
			canvas.drawCircle(x, y, buchang * 33 / 100, whitePaint);
			canvas.drawCircle(m, n, buchang * 47 / 100, blackPaint);
			canvas.drawCircle(m, n, buchang * 41 / 100, whitePaint);
			canvas.drawCircle(m, n, buchang * 37 / 100, blackPaint);
			canvas.drawCircle(m, n, buchang * 33 / 100, whitePaint);
			if (i == 0 || i == 8) {
				canvas.drawText("车", x - buchang * 24 / 100, y + buchang * 23
						/ 100, fugaiPaint);
				canvas.drawText("车", m - buchang * 24 / 100, n + buchang * 23
						/ 100, blackPaint);
			}//
			else if (i == 1 || i == 7) {
				canvas.drawText("马", x - buchang * 24 / 100, y + buchang * 23
						/ 100, fugaiPaint);
				canvas.drawText("马", m - buchang * 24 / 100, n + buchang * 23
						/ 100, blackPaint);
			}//
			else if (i == 2 || i == 6) {
				canvas.drawText("相", x - buchang * 24 / 100, y + buchang * 23
						/ 100, fugaiPaint);
				canvas.drawText("象", m - buchang * 24 / 100, n + buchang * 23
						/ 100, blackPaint);
			}//
			else if (i == 3 || i == 5) {
				canvas.drawText("仕", x - buchang * 24 / 100, y + buchang * 23
						/ 100, fugaiPaint);
				canvas.drawText("士", m - buchang * 24 / 100, n + buchang * 23
						/ 100, blackPaint);
			}//
			else if (i == 4) {
				canvas.drawText("帅", x - buchang * 24 / 100, y + buchang * 23
						/ 100, fugaiPaint);
				canvas.drawText("将", m - buchang * 24 / 100, n + buchang * 23
						/ 100, blackPaint);
			}//
			else if (i == 14 || i == 15) {
				canvas.drawText("炮", x - buchang * 24 / 100, y + buchang * 23
						/ 100, fugaiPaint);
				canvas.drawText("炮", m - buchang * 24 / 100, n + buchang * 23
						/ 100, blackPaint);
			}//
			else if (i == 9 || i == 10 || i == 11 || i == 12 || i == 13) {
				canvas.drawText("兵", x - buchang * 24 / 100, y + buchang * 23
						/ 100, fugaiPaint);
				canvas.drawText("卒", m - buchang * 24 / 100, n + buchang * 23
						/ 100, blackPaint);
			}//
		}

		else {
			canvas.drawCircle(m, n, buchang * 47 / 100, fugaiPaint);
			canvas.drawCircle(m, n, buchang * 41 / 100, whitePaint);
			canvas.drawCircle(m, n, buchang * 37 / 100, fugaiPaint);
			canvas.drawCircle(m, n, buchang * 33 / 100, whitePaint);
			canvas.drawCircle(x, y, buchang * 47 / 100, redPaint);
			canvas.drawCircle(x, y, buchang * 41 / 100, whitePaint);
			canvas.drawCircle(x, y, buchang * 37 / 100, redPaint);
			canvas.drawCircle(x, y, buchang * 33 / 100, whitePaint);
			if (i == 0 || i == 8) {
				canvas.drawText("车", m - buchang * 24 / 100, n + buchang * 23
						/ 100, fugaiPaint);
				canvas.drawText("车", x - buchang * 24 / 100, y + buchang * 23
						/ 100, redPaint);
			}//
			else if (i == 1 || i == 7) {
				canvas.drawText("马", m - buchang * 24 / 100, n + buchang * 23
						/ 100, fugaiPaint);
				canvas.drawText("马", x - buchang * 24 / 100, y + buchang * 23
						/ 100, redPaint);
			}//
			else if (i == 2 || i == 6) {
				canvas.drawText("象", m - buchang * 24 / 100, n + buchang * 23
						/ 100, fugaiPaint);
				canvas.drawText("相", x - buchang * 24 / 100, y + buchang * 23
						/ 100, redPaint);
			}//
			else if (i == 3 || i == 5) {
				canvas.drawText("士", m - buchang * 24 / 100, n + buchang * 23
						/ 100, fugaiPaint);
				canvas.drawText("仕", x - buchang * 24 / 100, y + buchang * 23
						/ 100, redPaint);
			}//
			else if (i == 4) {
				canvas.drawText("将", m - buchang * 24 / 100, n + buchang * 23
						/ 100, fugaiPaint);
				canvas.drawText("帅", x - buchang * 24 / 100, y + buchang * 23
						/ 100, redPaint);
			}//
			else if (i == 14 || i == 15) {
				canvas.drawText("炮", m - buchang * 24 / 100, n + buchang * 23
						/ 100, fugaiPaint);
				canvas.drawText("炮", x - buchang * 24 / 100, y + buchang * 23
						/ 100, redPaint);
			}//
			else if (i == 9 || i == 10 || i == 11 || i == 12 || i == 13) {
				canvas.drawText("卒", m - buchang * 24 / 100, n + buchang * 23
						/ 100, fugaiPaint);
				canvas.drawText("兵", x - buchang * 24 / 100, y + buchang * 23
						/ 100, redPaint);
			}//
		}
	}

	public void che(Canvas canvas, int i) {
		int x = startX + RedLoc[i][0] * buchang;
		int y = startY + RedLoc[i][1] * buchang;

		int m = startX + BlackLoc[i][0] * buchang;
		int n = startY + BlackLoc[i][1] * buchang;
		fugai(canvas, m, n, x, y);
		canvas.drawText("车", m - buchang * 24 / 100, n + buchang * 23 / 100,
				blackPaint);
		canvas.drawText("车", x - buchang * 24 / 100, y + buchang * 23 / 100,
				redPaint);

	}

	public boolean rche(int i, int j) {
		// 竖移
		int m = 0;
		m = RedLoc[Slt][0];
		int n = 0;
		n = RedLoc[Slt][1];
		if (i == m && j != n) {
			int min = -1;
			int max = 10;
			min = mins(Slt);
			max = maxx(Slt);
			if (j < n) {
				if (j > min) {
					RedLoc[Slt][1] = j;
					result = true;
				}//
				else if (j == min) {
					for (int k = 0; k <= 15; k++) {
						if (BlackLoc[k][0] == i && BlackLoc[k][1] == j) {
							BlackLoc[k][0] = 9;
							BlackLoc[k][1] = 10;
							RedLoc[Slt][1] = j;
							result = true;
							break;
						} //
						else if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
							result = false;
							break;
						}
					}

				} //
				else {
					result = false;
				}
			}//
			else if (j > n) {
				if (j < max) {
					RedLoc[Slt][1] = j;
					result = true;
				}//
				else if (j == max) {
					for (int k = 0; k <= 15; k++) {
						if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
							result = false;
							break;
						} else if (BlackLoc[k][0] == i && BlackLoc[k][1] == j) {
							BlackLoc[k][0] = 9;
							BlackLoc[k][1] = 10;
							RedLoc[Slt][1] = j;
							result = true;
							break;
						}
					}
				} //
				else {
					result = false;
				}
			} //
			else {
				result = false;
			}

		}
		// F
		// 横移
		else if (i != m && j == n) {
			int min = -1;
			int max = 9;
			min = minz(Slt);
			max = maxy(Slt);
			if (i < m) {
				if (i > min) {
					RedLoc[Slt][0] = i;
					result = true;
				} //
				else if (i == min) {
					for (int k = 0; k <= 15; k++) {
						if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
							result = false;
							break;
						}
						if (BlackLoc[k][0] == i && BlackLoc[k][1] == j) {
							BlackLoc[k][0] = 9;
							BlackLoc[k][1] = 10;
							RedLoc[Slt][0] = i;
							result = true;
							break;
						}
					}

				}//
				else {
					result = false;
				}
			} //
			else if (i > m) {
				if (i < max) {
					RedLoc[Slt][0] = i;
					result = true;
				} //
				else if (i == max) {
					for (int k = 0; k <= 15; k++) {
						if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
							result = false;
							break;
						}
						if (BlackLoc[k][0] == i && BlackLoc[k][1] == j) {
							BlackLoc[k][0] = 9;
							BlackLoc[k][1] = 10;
							RedLoc[Slt][0] = i;
							result = true;
							break;
						}
					}
				} //
				else {
					result = false;
				}
			}//
			else {
				result = false;
			}
		}
		// 点击错误、重新点击取消
		else {
			result = false;
		}
		return result;

	}

	public boolean rcheb(int i, int j) {
		// 竖移
		int m = 0;
		m = BlackLoc[Slt][0];
		int n = 0;
		n = BlackLoc[Slt][1];
		if (i == m && j != n) {
			int min = -1;
			int max = 10;
			min = mins(Slt);
			max = maxx(Slt);
			if (j < n) {
				if (j > min) {
					BlackLoc[Slt][1] = j;
					result = true;
				}//
				else if (j == min) {
					for (int k = 0; k <= 15; k++) {
						if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
							RedLoc[k][0] = 9;
							RedLoc[k][1] = 10;
							BlackLoc[Slt][1] = j;
							result = true;
							break;
						} //
						else if (BlackLoc[k][0] == i && BlackLoc[k][1] == j) {
							result = false;
							break;
						}
					}

				} //
				else {
					result = false;
				}
			}//
			else if (j > n) {
				if (j < max) {
					BlackLoc[Slt][1] = j;
					result = true;
				}//
				else if (j == max) {
					for (int k = 0; k <= 15; k++) {
						if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
							RedLoc[k][0] = 9;
							RedLoc[k][1] = 10;
							BlackLoc[Slt][1] = j;
							result = true;
							break;
						} //
						else if (BlackLoc[k][0] == i && BlackLoc[k][1] == j) {
							result = false;
							break;
						}
					}
				} //
				else {
					result = false;
				}
			} //
			else {
				result = false;
			}

		}
		// F
		// 横移
		else if (i != m && j == n) {
			int min = -1;
			int max = 9;
			min = minz(Slt);
			max = maxy(Slt);
			if (i < m) {
				if (i > min) {
					BlackLoc[Slt][0] = i;
					result = true;
				} //
				else if (i == min) {
					for (int k = 0; k <= 15; k++) {
						if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
							RedLoc[k][0] = 9;
							RedLoc[k][1] = 10;
							BlackLoc[Slt][0] = i;
							result = true;
							break;
						} else if (BlackLoc[k][0] == i && BlackLoc[k][1] == j) {
							result = false;
							break;
						}
					}

				}//
				else {
					result = false;
				}
			} //
			else if (i > m) {
				if (i < max) {
					BlackLoc[Slt][0] = i;
					result = true;
				} //
				else if (i == max) {
					for (int k = 0; k <= 15; k++) {
						if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
							RedLoc[k][0] = 9;
							RedLoc[k][1] = 10;
							BlackLoc[Slt][0] = i;
							result = true;
							break;
						} else if (BlackLoc[k][0] == i && BlackLoc[k][1] == j) {
							result = false;
							break;
						}
					}
				} //
				else {
					result = false;
				}
			}//
			else {
				result = false;
			}
		}
		// 点击错误、重新点击取消
		else {
			result = false;
		}
		return result;
	}

	public void ma(Canvas canvas, int i) {
		int x = startX + RedLoc[i][0] * buchang;
		int y = startY + RedLoc[i][1] * buchang;

		int m = startX + BlackLoc[i][0] * buchang;
		int n = startY + BlackLoc[i][1] * buchang;
		fugai(canvas, m, n, x, y);
		canvas.drawText("马", x - buchang * 24 / 100, y + buchang * 23 / 100,
				redPaint);
		canvas.drawText("马", m - buchang * 24 / 100, n + buchang * 23 / 100,
				blackPaint);

	}

	public boolean rma(int i, int j) {
		// 上
		boolean beijiao;
		beijiao = false;
		boolean zhanwei;
		zhanwei = false;
		int m = 0;
		m = RedLoc[Slt][0];
		int n = 0;
		n = RedLoc[Slt][1];
		if ((i == m + 1 && j == n - 2) || (i == m - 1 && j == n - 2)) {
			for (int k = 0; k <= 15; k++) {
				if ((m == RedLoc[k][0] && n == RedLoc[k][1] + 1)
						|| (m == BlackLoc[k][0] && n == BlackLoc[k][1] + 1)) {
					beijiao = true;
					result = false;
					break;
				}
			}
			if (!beijiao) {
				for (int k = 0; k <= 15; k++) {
					if (i == BlackLoc[k][0] && j == BlackLoc[k][1]) {
						BlackLoc[k][0] = 9;
						BlackLoc[k][1] = 10;
						RedLoc[Slt][0] = i;
						RedLoc[Slt][1] = j;
						zhanwei = true;
						result = true;
						break;
					} else if (i == RedLoc[k][0] && j == RedLoc[k][1]) {
						zhanwei = true;
						result = false;
						break;
					}
				}
				if (!zhanwei) {
					RedLoc[Slt][0] = i;
					RedLoc[Slt][1] = j;
					result = true;
				}
			}
		} //
		else if ((i == m - 2 && j == n - 1) || (i == m - 2 && j == n + 1)) {
			for (int k = 0; k <= 15; k++) {
				if ((m == RedLoc[k][0] + 1 && n == RedLoc[k][1])
						|| (m == BlackLoc[k][0] + 1 && n == BlackLoc[k][1])) {
					beijiao = true;
					result = false;
					break;
				}
			}
			if (!beijiao) {
				for (int k = 0; k <= 15; k++) {
					if (i == BlackLoc[k][0] && j == BlackLoc[k][1]) {
						BlackLoc[k][0] = 9;
						BlackLoc[k][1] = 10;
						RedLoc[Slt][0] = i;
						RedLoc[Slt][1] = j;
						zhanwei = true;
						result = true;
						break;
					} else if (i == RedLoc[k][0] && j == RedLoc[k][1]) {
						zhanwei = true;
						result = false;
						break;
					}
				}
				if (!zhanwei) {
					RedLoc[Slt][0] = i;
					RedLoc[Slt][1] = j;
					result = true;
				}
			}
		}//

		else if ((i == m - 1 && j == n + 2) || (i == m + 1 && j == n + 2)) {
			for (int k = 0; k <= 15; k++) {
				if ((m == RedLoc[k][0] && n == RedLoc[k][1] - 1)
						|| (m == BlackLoc[k][0] && n == BlackLoc[k][1] - 1)) {
					beijiao = true;
					result = false;
					break;
				}
			}
			if (!beijiao) {
				for (int k = 0; k <= 15; k++) {
					if (i == BlackLoc[k][0] && j == BlackLoc[k][1]) {
						BlackLoc[k][0] = 9;
						BlackLoc[k][1] = 10;
						RedLoc[Slt][0] = i;
						RedLoc[Slt][1] = j;
						zhanwei = true;
						result = true;
						break;
					} else if (i == RedLoc[k][0] && j == RedLoc[k][1]) {
						zhanwei = true;
						result = false;
						break;
					}
				}
				if (!zhanwei) {
					RedLoc[Slt][0] = i;
					RedLoc[Slt][1] = j;
					result = true;
				}
			}
		}//

		else if ((i == m + 2 && j == n - 1) || (i == m + 2 && j == n + 1)) {
			for (int k = 0; k <= 15; k++) {
				if ((m == RedLoc[k][0] - 1 && n == RedLoc[k][1])
						|| (m == BlackLoc[k][0] - 1 && n == BlackLoc[k][1])) {
					beijiao = true;
					result = false;
					break;
				}
			}
			if (!beijiao) {
				for (int k = 0; k <= 15; k++) {
					if (i == BlackLoc[k][0] && j == BlackLoc[k][1]) {
						BlackLoc[k][0] = 9;
						BlackLoc[k][1] = 10;
						RedLoc[Slt][0] = i;
						RedLoc[Slt][1] = j;
						zhanwei = true;
						result = true;
						break;
					} else if (i == RedLoc[k][0] && j == RedLoc[k][1]) {
						zhanwei = true;
						result = false;
						break;
					}
				}
				if (!zhanwei) {
					RedLoc[Slt][0] = i;
					RedLoc[Slt][1] = j;
					result = true;
				}
			}
		}// 错误点击，重新点击取消
		else {
			result = false;
		}

		return result;

	}

	public boolean rmab(int i, int j) {
		// 上
		boolean beijiao;
		beijiao = false;
		boolean zhanwei;
		zhanwei = false;
		int m = 0;
		m = BlackLoc[Slt][0];
		int n = 0;
		n = BlackLoc[Slt][1];
		if ((i == m + 1 && j == n - 2) || (i == m - 1 && j == n - 2)) {
			// 判断绊马脚
			for (int k = 0; k <= 15; k++) {
				if ((m == RedLoc[k][0] && n == RedLoc[k][1] + 1)
						|| (m == BlackLoc[k][0] && n == BlackLoc[k][1] + 1)) {
					beijiao = true;
					result = false;
					break;
				}
			}
			// F
			if (!beijiao) {
				for (int k = 0; k <= 15; k++) {
					if (i == BlackLoc[k][0] && j == BlackLoc[k][1]) {
						zhanwei = true;
						result = false;
						break;
					} else if (i == RedLoc[k][0] && j == RedLoc[k][1]) {
						RedLoc[k][0] = 9;
						RedLoc[k][1] = 10;
						BlackLoc[Slt][0] = i;
						BlackLoc[Slt][1] = j;
						zhanwei = true;
						result = true;
						break;
					}
				}
				if (!zhanwei) {
					BlackLoc[Slt][0] = i;
					BlackLoc[Slt][1] = j;
					result = true;
				}
			}
		} //
		else if ((i == m - 2 && j == n - 1) || (i == m - 2 && j == n + 1)) {
			for (int k = 0; k <= 15; k++) {
				if ((m == RedLoc[k][0] + 1 && n == RedLoc[k][1])
						|| (m == BlackLoc[k][0] + 1 && n == BlackLoc[k][1])) {
					beijiao = true;
					result = false;
					break;
				}
			}
			if (!beijiao) {
				for (int k = 0; k <= 15; k++) {
					if (i == BlackLoc[k][0] && j == BlackLoc[k][1]) {
						zhanwei = true;
						result = false;
						break;
					} else if (i == RedLoc[k][0] && j == RedLoc[k][1]) {
						RedLoc[k][0] = 9;
						RedLoc[k][1] = 10;
						BlackLoc[Slt][0] = i;
						BlackLoc[Slt][1] = j;
						zhanwei = true;
						result = true;
						break;
					}
				}
				if (!zhanwei) {
					BlackLoc[Slt][0] = i;
					BlackLoc[Slt][1] = j;
					result = true;
				}
			}
		}//

		else if ((i == m - 1 && j == n + 2) || (i == m + 1 && j == n + 2)) {
			for (int k = 0; k <= 15; k++) {
				if ((m == RedLoc[k][0] && n == RedLoc[k][1] - 1)
						|| (m == BlackLoc[k][0] && n == BlackLoc[k][1] - 1)) {
					beijiao = true;
					result = false;
					break;
				}
			}
			if (!beijiao) {
				for (int k = 0; k <= 15; k++) {
					if (i == BlackLoc[k][0] && j == BlackLoc[k][1]) {
						zhanwei = true;
						result = false;
						break;
					} else if (i == RedLoc[k][0] && j == RedLoc[k][1]) {
						RedLoc[k][0] = 9;
						RedLoc[k][1] = 10;
						BlackLoc[Slt][0] = i;
						BlackLoc[Slt][1] = j;
						zhanwei = true;
						result = true;
						break;
					}
				}
				if (!zhanwei) {
					BlackLoc[Slt][0] = i;
					BlackLoc[Slt][1] = j;
					result = true;
				}
			}
		}//

		else if ((i == m + 2 && j == n - 1) || (i == m + 2 && j == n + 1)) {
			for (int k = 0; k <= 15; k++) {
				if ((m == RedLoc[k][0] - 1 && n == RedLoc[k][1])
						|| (m == BlackLoc[k][0] - 1 && n == BlackLoc[k][1])) {
					beijiao = true;
					result = false;
					break;
				}
			}
			if (!beijiao) {
				for (int k = 0; k <= 15; k++) {
					if (i == BlackLoc[k][0] && j == BlackLoc[k][1]) {
						zhanwei = true;
						result = false;
						break;
					} else if (i == RedLoc[k][0] && j == RedLoc[k][1]) {
						RedLoc[k][0] = 9;
						RedLoc[k][1] = 10;
						BlackLoc[Slt][0] = i;
						BlackLoc[Slt][1] = j;
						zhanwei = true;
						result = true;
						break;
					}
				}
				if (!zhanwei) {
					BlackLoc[Slt][0] = i;
					BlackLoc[Slt][1] = j;
					result = true;
				}
			}
		}// 错误点击，重新点击取消
		else {
			result = false;
		}
		return result;
	}

	public void pao(Canvas canvas, int i) {
		int x = startX + RedLoc[i][0] * buchang;
		int y = startY + RedLoc[i][1] * buchang;
		int m = startX + BlackLoc[i][0] * buchang;
		int n = startY + BlackLoc[i][1] * buchang;
		fugai(canvas, m, n, x, y);
		canvas.drawText("炮", m - buchang * 24 / 100, n + buchang * 23 / 100,
				blackPaint);
		canvas.drawText("炮", x - buchang * 24 / 100, y + buchang * 23 / 100,
				redPaint);
	}

	public boolean rpao(int i, int j) {
		// 竖移
		int m = 0;
		m = RedLoc[Slt][0];
		int n = 0;
		n = RedLoc[Slt][1];
		if (i == m && j != n) {
			int min = -1;
			int max = 10;
			min = mins(Slt);
			max = maxx(Slt);
			if (j < n) {
				if (j > min) {
					RedLoc[Slt][1] = j;
					result = true;
				}//
				else if (j < min) {
					// 找到炮台棋子
					boolean tair;
					tair = false;
					int k = 0;
					for (k = 0; k <= 15; k++) {
						if (BlackLoc[k][0] == i && BlackLoc[k][1] == min) {
							tair = false;
							break;
						}
						//
						else if (RedLoc[k][0] == i && RedLoc[k][1] == min) {
							tair = true;
							break;
						}
					}
					if (tair) {
						int shang = -1;
						shang = mins(k);
						if (j == shang) {
							//
							for (int kl = 0; kl <= 15; kl++) {
								if (RedLoc[kl][0] == i && RedLoc[kl][1] == j) {
									result = false;
									break;
								} else if (BlackLoc[kl][0] == i
										&& BlackLoc[kl][1] == j) {
									BlackLoc[kl][0] = 9;
									BlackLoc[kl][1] = 10;
									RedLoc[Slt][1] = j;
									result = true;
									break;
								}
							}
						} else {
							result = false;
						}
					} else {
						MoveSide = false;
						int shang = -1;
						shang = mins(k);
						if (j == shang) {
							//
							for (int kl = 0; kl <= 15; kl++) {
								if (RedLoc[kl][0] == i && RedLoc[kl][1] == j) {
									result = false;
									break;
								} else if (BlackLoc[kl][0] == i
										&& BlackLoc[kl][1] == j) {
									BlackLoc[kl][0] = 9;
									BlackLoc[kl][1] = 10;
									RedLoc[Slt][1] = j;
									result = true;
									break;
								}
							}
						} else {
							result = false;
						}
						MoveSide = true;
					}
				} //
				else {
					result = false;
				}
			}//
			else if (j > n) {
				if (j < max) {
					RedLoc[Slt][1] = j;
					result = true;
				}//
				else if (j > max) {
					boolean tair = false;
					int k = 0;
					for (k = 0; k <= 15; k++) {
						if (RedLoc[k][0] == i && RedLoc[k][1] == max) {
							tair = true;
							break;
						} else if (BlackLoc[k][0] == i && BlackLoc[k][1] == max) {
							tair = false;
							break;
						}
					}
					if (tair) {
						int shang = 10;
						shang = maxx(k);
						if (j == shang) {
							//
							for (int kl = 0; kl <= 15; kl++) {
								if (RedLoc[kl][0] == i && RedLoc[kl][1] == j) {
									result = false;
									break;
								} else if (BlackLoc[kl][0] == i
										&& BlackLoc[kl][1] == j) {
									BlackLoc[kl][0] = 9;
									BlackLoc[kl][1] = 10;
									RedLoc[Slt][1] = j;
									result = true;
									break;
								}
							}
						} else {
							result = false;
						}
					} else {
						MoveSide = false;
						int shang = 10;
						shang = maxx(k);
						if (j == shang) {
							//
							for (int kl = 0; kl <= 15; kl++) {
								if (RedLoc[kl][0] == i && RedLoc[kl][1] == j) {
									result = false;
									break;
								} else if (BlackLoc[kl][0] == i
										&& BlackLoc[kl][1] == j) {
									BlackLoc[kl][0] = 9;
									BlackLoc[kl][1] = 10;
									RedLoc[Slt][1] = j;
									result = true;
									break;
								}
							}
						} else {
							result = false;
						}
						MoveSide = true;
					}
				} //
				else {
					result = false;
				}
			} //
			else {
				result = false;
			}
		}
		// F
		// 横移
		else if (i != m && j == n) {
			int min = -1;
			int max = 9;
			min = minz(Slt);
			max = maxy(Slt);
			if (i < m) {
				if (i > min) {
					RedLoc[Slt][0] = i;
					result = true;
				} //
				else if (i < min) {
					boolean tair = false;
					int k = 0;
					for (k = 0; k <= 15; k++) {
						// 找炮台
						if (RedLoc[k][0] == min && RedLoc[k][1] == j) {
							tair = true;
							break;
						} else if (BlackLoc[k][0] == min && BlackLoc[k][1] == j) {
							tair = false;
							break;
						}
					}
					if (tair) {
						int zuo = -1;
						zuo = minz(k);
						if (i == zuo) {
							for (int kl = 0; kl <= 15; kl++) {
								if (RedLoc[kl][0] == i && RedLoc[kl][1] == j) {
									result = false;
									break;
								} else if (BlackLoc[kl][0] == i
										&& BlackLoc[kl][1] == j) {
									BlackLoc[kl][0] = 9;
									BlackLoc[kl][1] = 10;
									RedLoc[Slt][0] = i;
									result = true;
									break;
								}
							}
						} else {
							result = false;
						}
					} else {
						int zuo = -1;
						MoveSide = false;
						zuo = minz(k);
						MoveSide = true;
						if (i == zuo) {
							for (int kl = 0; kl <= 15; kl++) {
								if (RedLoc[kl][0] == i && RedLoc[kl][1] == j) {
									result = false;
									break;
								} else if (BlackLoc[kl][0] == i
										&& BlackLoc[kl][1] == j) {
									BlackLoc[kl][0] = 9;
									BlackLoc[kl][1] = 10;
									RedLoc[Slt][0] = i;
									result = true;
									break;
								}
							}
						} else {
							result = false;
						}
					}
				}//
				else {
					result = false;
				}
			} //
			else if (i > m) {
				if (i < max) {
					RedLoc[Slt][0] = i;
					result = true;
				} //
				else if (i > max) {
					int k = 0;
					boolean tair;
					tair = false;
					for (k = 0; k <= 15; k++) {
						if (RedLoc[k][0] == max && RedLoc[k][1] == j) {
							tair = true;
							break;
						} else if (BlackLoc[k][0] == max && BlackLoc[k][1] == j) {
							tair = false;
							break;
						}
					}
					if (tair) {
						int you = 9;
						you = maxy(k);
						if (i == you) {
							for (int kl = 0; kl <= 15; kl++) {
								if (RedLoc[kl][0] == i && RedLoc[kl][1] == j) {
									result = false;
									break;
								} else if (BlackLoc[kl][0] == i
										&& BlackLoc[kl][1] == j) {
									BlackLoc[kl][0] = 9;
									BlackLoc[kl][1] = 10;
									RedLoc[Slt][0] = i;
									result = true;
									break;
								}
							}
						} else {
							result = false;
						}
					} else {
						int you = 9;
						MoveSide = false;
						you = maxy(k);
						MoveSide = true;
						if (i == you) {
							for (int kl = 0; kl <= 15; kl++) {
								if (RedLoc[kl][0] == i && RedLoc[kl][1] == j) {
									result = false;
									break;
								} else if (BlackLoc[kl][0] == i
										&& BlackLoc[kl][1] == j) {
									BlackLoc[kl][0] = 9;
									BlackLoc[kl][1] = 10;
									RedLoc[Slt][0] = i;
									result = true;
									break;
								}
							}
						} else {
							result = false;
						}
					}
				} //
				else {
					result = false;
				}
			}//
			else {
				result = false;
			}
		}
		return result;
	}

	public boolean rpaob(int i, int j) {
		// 竖移
		int m = 0;
		m = BlackLoc[Slt][0];
		int n = 0;
		n = BlackLoc[Slt][1];
		if (i == m && j != n) {
			int min = -1;
			int max = 10;
			min = mins(Slt);
			max = maxx(Slt);
			if (j < n) {
				if (j > min) {
					BlackLoc[Slt][1] = j;
					result = true;
				}//
				else if (j < min) {
					// 找到炮台棋子
					boolean tair;
					tair = false;
					int k = 0;
					for (k = 0; k <= 15; k++) {
						if (BlackLoc[k][0] == i && BlackLoc[k][1] == min) {
							tair = true;
							break;
						}
						//
						else if (RedLoc[k][0] == i && RedLoc[k][1] == min) {
							tair = false;
							break;
						}
					}
					if (tair) {
						int shang = -1;
						shang = mins(k);
						if (shang == j) {
							for (int kl = 0; kl <= 15; kl++) {
								if (BlackLoc[kl][0] == i
										&& BlackLoc[kl][1] == j) {
									result = false;
									break;
								} else if (RedLoc[kl][0] == i
										&& RedLoc[kl][1] == j) {
									RedLoc[kl][0] = 9;
									RedLoc[kl][0] = 10;
									BlackLoc[Slt][1] = j;
									result = true;
									break;
								}
							}
						} else {
							result = false;
						}
					} else {
						MoveSide = true;
						int shang = -1;
						shang = mins(k);
						MoveSide = false;
						if (j == shang) {
							for (int kl = 0; kl <= 15; kl++) {
								if (BlackLoc[kl][0] == i
										&& BlackLoc[kl][1] == j) {
									result = false;
									break;
								} else if (RedLoc[kl][0] == i
										&& RedLoc[kl][1] == j) {
									RedLoc[kl][0] = 9;
									RedLoc[kl][0] = 10;
									BlackLoc[Slt][1] = j;
									result = true;
									break;
								}
							}
						} else {
							result = false;
						}
					}
				} //
				else {
					result = false;
				}
			}//
			else if (j > n) {
				if (j < max) {
					BlackLoc[Slt][1] = j;
					result = true;
				}//
				else if (j > max) {
					boolean tair = false;
					int k = 0;
					for (k = 0; k <= 15; k++) {
						if (RedLoc[k][0] == i && RedLoc[k][1] == max) {
							tair = false;
							break;
						} else if (BlackLoc[k][0] == i && BlackLoc[k][1] == max) {
							tair = true;
							break;
						}
					}
					if (tair) {
						int shang = 10;
						shang = maxx(k);
						if (j == shang) {
							for (int kl = 0; kl <= 15; kl++) {
								if (BlackLoc[kl][0] == i
										&& BlackLoc[kl][1] == j) {
									result = false;
									break;
								} else if (RedLoc[kl][0] == i
										&& RedLoc[kl][1] == j) {
									RedLoc[kl][0] = 9;
									RedLoc[kl][0] = 10;
									BlackLoc[Slt][1] = j;
									result = true;
									break;
								}
							}
						} else {
							result = false;
						}
					} else {
						MoveSide = true;
						int shang = 10;
						shang = maxx(k);
						if (j == shang) {
							for (int kl = 0; kl <= 15; kl++) {
								if (BlackLoc[kl][0] == i
										&& BlackLoc[kl][1] == j) {
									result = false;
									break;
								} else if (RedLoc[kl][0] == i
										&& RedLoc[kl][1] == j) {
									RedLoc[kl][0] = 9;
									RedLoc[kl][0] = 10;
									BlackLoc[Slt][1] = j;
									result = true;
									break;
								}
							}
						} else {
							result = false;
						}
						MoveSide = false;
					}
				} //
				else {
					result = false;
				}
			} //
			else {
				result = false;
			}
		}
		// F
		// 横移
		else if (i != m && j == n) {
			int min = -1;
			int max = 9;
			min = minz(Slt);
			max = maxy(Slt);
			if (i < m) {
				if (i > min) {
					BlackLoc[Slt][0] = i;
					result = true;
				} //
				else if (i < min) {
					boolean tair = false;
					int k = 0;
					for (k = 0; k <= 15; k++) {
						if (RedLoc[k][0] == min && RedLoc[k][1] == j) {
							tair = false;
							break;
						} else if (BlackLoc[k][0] == min && BlackLoc[k][1] == j) {
							tair = true;
							break;
						}
					}
					if (tair) {
						int zuo = -1;
						zuo = minz(k);
						if (i == zuo) {
							for (int kl = 0; kl <= 15; kl++) {
								if (BlackLoc[kl][0] == i
										&& BlackLoc[kl][1] == j) {
									result = false;
									break;
								} else if (RedLoc[kl][0] == i
										&& RedLoc[kl][1] == j) {
									RedLoc[kl][0] = 9;
									RedLoc[kl][0] = 10;
									BlackLoc[Slt][0] = i;
									result = true;
									break;
								}
							}
						} else {
							result = false;
						}
					} else {
						// 转化用于min
						MoveSide = true;
						int zuo = -1;
						zuo = minz(k);
						if (i == zuo) {
							for (int kl = 0; kl <= 15; kl++) {
								if (BlackLoc[kl][0] == i
										&& BlackLoc[kl][1] == j) {
									result = false;
									break;
								} else if (RedLoc[kl][0] == i
										&& RedLoc[kl][1] == j) {
									RedLoc[kl][0] = 9;
									RedLoc[kl][0] = 10;
									BlackLoc[Slt][0] = i;
									result = true;
									break;
								}
							}
						} else {
							result = false;
						}
						MoveSide = false;
					}
				}//
				else {
					result = false;
				}
			} //
			else if (i > m) {
				if (i < max) {
					BlackLoc[Slt][0] = i;
					result = true;
				} //
				else if (i > max) {
					int k = 0;
					boolean tair = false;
					for (k = 0; k <= 15; k++) {
						if (RedLoc[k][0] == max && RedLoc[k][1] == j) {
							tair = false;
							break;
						} else if (BlackLoc[k][0] == max && BlackLoc[k][1] == j) {
							tair = true;
							break;
						}
					}
					if (tair) {
						int you = 9;
						you = maxy(k);
						if (i == you) {
							for (int kl = 0; kl <= 15; kl++) {
								if (BlackLoc[kl][0] == i
										&& BlackLoc[kl][1] == j) {
									result = false;
									break;
								} else if (RedLoc[kl][0] == i
										&& RedLoc[kl][1] == j) {
									RedLoc[kl][0] = 9;
									RedLoc[kl][0] = 10;
									BlackLoc[Slt][0] = i;
									result = true;
									break;
								}
							}
						} else {
							result = false;
						}
					} else {
						MoveSide = true;
						int you = 9;
						you = maxy(k);
						MoveSide = false;
						if (i == you) {
							for (int kl = 0; kl <= 15; kl++) {
								if (BlackLoc[kl][0] == i
										&& BlackLoc[kl][1] == j) {
									result = false;
									break;
								} else if (RedLoc[kl][0] == i
										&& RedLoc[kl][1] == j) {
									RedLoc[kl][0] = 9;
									RedLoc[kl][0] = 10;
									BlackLoc[Slt][0] = i;
									result = true;
									break;
								}
							}
						} else {
							result = false;
						}
					}
				} //
				else {
					result = false;
				}
			}//
			else {
				result = false;
			}
		}
		return result;
	}

	public void zu(Canvas canvas, int i) {
		int x = startX + RedLoc[i][0] * buchang;
		int y = startY + RedLoc[i][1] * buchang;

		int m = startX + BlackLoc[i][0] * buchang;
		int n = startY + BlackLoc[i][1] * buchang;
		fugai(canvas, m, n, x, y);
		canvas.drawText("兵", x - buchang * 24 / 100, y + buchang * 23 / 100,
				redPaint);
		canvas.drawText("卒", m - buchang * 24 / 100, n + buchang * 23 / 100,
				blackPaint);
	}

	public boolean rzu(int i, int j) {
		int m = 0;
		m = RedLoc[Slt][0];
		int n = 0;
		n = RedLoc[Slt][1];
		// 未过河
		boolean zhan;
		zhan = false;
		if (n == 5 || n == 6) {
			if (m == i && n == j + 1) {

				for (int k = 0; k <= 15; k++) {
					if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
						result = false;
						zhan = true;
						break;
					} else if (BlackLoc[k][0] == i && BlackLoc[k][1] == j) {
						BlackLoc[k][0] = 9;
						BlackLoc[k][1] = 10;
						RedLoc[Slt][1] = j;
						result = true;
						zhan = true;
						break;
					}
				}
				if (!zhan) {
					RedLoc[Slt][1] = j;
					result = true;
				}
			} else {
				result = false;
			}
		} else if (n <= 4) {
			if (m == i && n == j + 1) {
				for (int k = 0; k <= 15; k++) {
					if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
						result = false;
						zhan = true;
						break;
					} else if (BlackLoc[k][0] == i && BlackLoc[k][1] == j) {
						BlackLoc[k][0] = 9;
						BlackLoc[k][1] = 10;
						RedLoc[Slt][1] = j;
						result = true;
						zhan = true;
						break;
					}
				}
				if (!zhan) {
					RedLoc[Slt][1] = j;
					result = true;
				}
			} else if (n == j && (m == i + 1 || m == i - 1)) {
				for (int k = 0; k <= 15; k++) {
					if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
						result = false;
						zhan = true;
						break;
					} else if (BlackLoc[k][0] == i && BlackLoc[k][1] == j) {
						BlackLoc[k][0] = 9;
						BlackLoc[k][1] = 10;
						RedLoc[Slt][0] = i;
						result = true;
						zhan = true;
						break;
					}
				}
				if (!zhan) {
					RedLoc[Slt][0] = i;
					result = true;
				}
			}// 点击错误、重新点击取消
			else {
				result = false;
			}
		}// 点击错误、重新点击取消
		else {
			result = false;
		}
		return result;
	}

	public boolean rzub(int i, int j) {
		int m = 0;
		m = BlackLoc[Slt][0];
		int n = 0;
		n = BlackLoc[Slt][1];
		// 未过河
		boolean zhan;
		zhan = false;
		if (n == 3 || n == 4) {
			if (m == i && n == j - 1) {
				for (int k = 0; k <= 15; k++) {
					if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
						RedLoc[k][0] = 9;
						RedLoc[k][1] = 10;
						BlackLoc[Slt][1] = j;
						result = true;
						zhan = true;
						break;
					} else if (BlackLoc[k][0] == i && BlackLoc[k][1] == j) {
						result = false;
						zhan = true;
						break;

					}
				}
				if (!zhan) {
					BlackLoc[Slt][1] = j;
					result = true;
				}
			} else {
				result = false;
			}
		} else if (n >= 5) {
			if (m == i && n == j - 1) {
				for (int k = 0; k <= 15; k++) {
					if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
						RedLoc[k][0] = 9;
						RedLoc[k][1] = 10;
						BlackLoc[Slt][1] = j;
						result = true;
						zhan = true;
						break;
					} else if (BlackLoc[k][0] == i && BlackLoc[k][1] == j) {
						result = false;
						zhan = true;
						break;

					}
				}
				if (!zhan) {
					BlackLoc[Slt][1] = j;
					result = true;
				}
			} else if (n == j && (m == i + 1 || m == i - 1)) {
				for (int k = 0; k <= 15; k++) {
					if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
						RedLoc[k][0] = 9;
						RedLoc[k][1] = 10;
						BlackLoc[Slt][0] = i;
						result = true;
						zhan = true;
						break;
					} else if (BlackLoc[k][0] == i && BlackLoc[k][1] == j) {
						result = false;
						zhan = true;
						break;

					}
				}
				if (!zhan) {
					BlackLoc[Slt][0] = i;
					result = true;
				}
			}// 点击错误、重新点击取消
			else {
				result = false;
			}
		}// 点击错误、重新点击取消
		else {
			result = false;
		}
		return result;
	}

	public void wang(Canvas canvas, int i) {
		int x = startX + RedLoc[i][0] * buchang;
		int y = startY + RedLoc[i][1] * buchang;

		int m = startX + BlackLoc[i][0] * buchang;
		int n = startY + BlackLoc[i][1] * buchang;
		fugai(canvas, m, n, x, y);
		canvas.drawText("帅", x - buchang * 24 / 100, y + buchang * 23 / 100,
				redPaint);
		canvas.drawText("将", m - buchang * 24 / 100, n + buchang * 23 / 100,
				blackPaint);
	}

	public boolean rshuai(int i, int j) {
		if ((i == 3 || i == 4 || i == 5) && (j == 7 || j == 8 || j == 9)) {
			int m = RedLoc[Slt][0];
			int n = RedLoc[Slt][1];
			int shang = -1;
			shang = mins(Slt);
			int xia = 10;
			xia = maxx(Slt);
			int zuo = -1;
			zuo = minz(Slt);
			int you = 9;
			you = maxy(Slt);
			// 竖移
			if (m == i && n != j) {
				if (n == j + 1) {
					if (j > shang) {
						RedLoc[Slt][1] = j;
						result = true;
					} else if (j == shang) {
						for (int k = 0; k <= 15; k++) {
							if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
								result = false;
								break;
							} else if (BlackLoc[k][0] == i
									&& BlackLoc[k][1] == j) {
								BlackLoc[k][0] = 9;
								BlackLoc[k][1] = 10;
								RedLoc[Slt][1] = j;
								result = true;
								break;
							}
						}
					} else {
						result = false;
					}
				} else if (n == j - 1) {
					if (j < xia) {
						RedLoc[Slt][1] = j;
						result = true;
					} else if (j == xia) {
						for (int k = 0; k <= 15; k++) {
							if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
								result = false;
								break;
							} else if (BlackLoc[k][0] == i
									&& BlackLoc[k][1] == j) {
								BlackLoc[k][0] = 9;
								BlackLoc[k][1] = 10;
								RedLoc[Slt][1] = j;
								result = true;
								break;
							}
						}
					} else {
						result = false;
					}
				} else {
					result = false;
				}
			} else if (m != i && j == n) {
				if (m == i + 1) {
					if (i > zuo) {
						RedLoc[Slt][0] = i;
						result = true;
					} else if (i == zuo) {
						for (int k = 0; k <= 15; k++) {
							if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
								result = false;
								break;
							} else if (BlackLoc[k][0] == i
									&& BlackLoc[k][1] == j) {
								BlackLoc[k][0] = 9;
								BlackLoc[k][1] = 10;
								RedLoc[Slt][0] = i;
								result = true;
								break;
							}
						}
					} else {
						result = false;
					}
				} else if (m == i - 1) {
					if (i < you) {
						RedLoc[Slt][0] = i;
						result = true;
					} else if (i == you) {
						for (int k = 0; k <= 15; k++) {
							if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
								result = false;
								break;
							} else if (BlackLoc[k][0] == i
									&& BlackLoc[k][1] == j) {
								BlackLoc[k][0] = 9;
								BlackLoc[k][1] = 10;
								RedLoc[Slt][0] = i;
								result = true;
								break;
							}
						}
					} else {
						result = false;
					}
				} else {
					result = false;
				}
			} else {
				result = false;
			}
		} else {
			result = false;
		}
		return result;
	}

	public boolean jiang(int i, int j) {
		if ((i == 3 || i == 4 || i == 5) && (j == 0 || j == 1 || j == 2)) {
			int m = BlackLoc[Slt][0];
			int n = BlackLoc[Slt][1];
			int shang = -1;
			shang = mins(Slt);
			int xia = 10;
			xia = maxx(Slt);
			int zuo = -1;
			zuo = minz(Slt);
			int you = 9;
			you = maxy(Slt);
			// 竖移
			if (m == i && n != j) {
				if (n == j + 1) {
					if (j > shang) {
						BlackLoc[Slt][1] = j;
						result = true;
					} else if (j == shang) {
						for (int k = 0; k <= 15; k++) {
							if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
								RedLoc[k][0] = 9;
								RedLoc[k][1] = 10;
								BlackLoc[Slt][1] = j;
								result = true;
								break;

							} else if (BlackLoc[k][0] == i
									&& BlackLoc[k][1] == j) {
								result = false;
								break;
							}
						}
					} else {
						result = false;
					}
				} else if (n == j - 1) {
					if (j < xia) {
						BlackLoc[Slt][1] = j;
						result = true;
					} else if (j == xia) {
						for (int k = 0; k <= 15; k++) {
							if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
								RedLoc[k][0] = 9;
								RedLoc[k][1] = 10;
								BlackLoc[Slt][1] = j;
								result = true;
								break;

							} else if (BlackLoc[k][0] == i
									&& BlackLoc[k][1] == j) {
								result = false;
								break;
							}
						}
					} else {
						result = false;
					}
				} else {
					result = false;
				}
			} else if (m != i && j == n) {
				if (m == i + 1) {
					if (i > zuo) {
						BlackLoc[Slt][0] = i;
						result = true;
					} else if (i == zuo) {
						for (int k = 0; k <= 15; k++) {
							if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
								RedLoc[k][0] = 9;
								RedLoc[k][1] = 10;
								BlackLoc[Slt][0] = i;
								result = true;
								break;

							} else if (BlackLoc[k][0] == i
									&& BlackLoc[k][1] == j) {
								result = false;
								break;
							}
						}
					} else {
						result = false;
					}
				} else if (m == i - 1) {
					if (i < you) {
						BlackLoc[Slt][0] = i;
						result = true;
					} else if (i == you) {
						for (int k = 0; k <= 15; k++) {
							if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
								RedLoc[k][0] = 9;
								RedLoc[k][1] = 10;
								BlackLoc[Slt][0] = i;
								result = true;
								break;

							} else if (BlackLoc[k][0] == i
									&& BlackLoc[k][1] == j) {
								result = false;
								break;
							}
						}
					} else {
						result = false;
					}
				} else {
					result = false;
				}
			} else {
				result = false;
			}
		} else {
			result = false;
		}
		return result;
	}

	public void xiang(Canvas canvas, int i) {
		int x = startX + RedLoc[i][0] * buchang;
		int y = startY + RedLoc[i][1] * buchang;

		int m = startX + BlackLoc[i][0] * buchang;
		int n = startY + BlackLoc[i][1] * buchang;
		fugai(canvas, m, n, x, y);
		canvas.drawText("相", x - buchang * 24 / 100, y + buchang * 23 / 100,
				redPaint);
		canvas.drawText("象", m - buchang * 24 / 100, n + buchang * 23 / 100,
				blackPaint);
	}

	public boolean rxiang(int i, int j) {
		if (j >= 5) {
			int m = 0;
			m = RedLoc[Slt][0];
			int n = 0;
			n = RedLoc[Slt][1];
			boolean beijiao;
			beijiao = false;
			boolean zhanwei;
			zhanwei = false;
			if (i == m - 2 && j == n - 2) {
				for (int k = 0; k <= 15; k++) {
					if ((m == RedLoc[k][0] + 1 && n == RedLoc[k][1] + 1)
							|| (m == BlackLoc[k][0] + 1 && n == BlackLoc[k][1] + 1)) {
						beijiao = true;
						result = false;
						break;
					}
				}
				if (!beijiao) {
					for (int k = 0; k <= 15; k++) {
						if (i == BlackLoc[k][0] && j == BlackLoc[k][1]) {
							BlackLoc[k][0] = 9;
							BlackLoc[k][1] = 10;
							RedLoc[Slt][0] = i;
							RedLoc[Slt][1] = j;
							zhanwei = true;
							result = true;
							break;
						} else if (i == RedLoc[k][0] && j == RedLoc[k][1]) {
							zhanwei = true;
							result = false;
							break;
						}
					}
					if (!zhanwei) {
						RedLoc[Slt][0] = i;
						RedLoc[Slt][1] = j;
						result = true;
					}
				}
			} else if (i == m - 2 && j == n + 2) {
				for (int k = 0; k <= 15; k++) {
					if ((m == RedLoc[k][0] + 1 && n == RedLoc[k][1] - 1)
							|| (m == BlackLoc[k][0] + 1 && n == BlackLoc[k][1] - 1)) {
						beijiao = true;
						result = false;
						break;
					}
				}
				if (!beijiao) {
					for (int k = 0; k <= 15; k++) {
						if (i == BlackLoc[k][0] && j == BlackLoc[k][1]) {
							BlackLoc[k][0] = 9;
							BlackLoc[k][1] = 10;
							RedLoc[Slt][0] = i;
							RedLoc[Slt][1] = j;
							zhanwei = true;
							result = true;
							break;
						} else if (i == RedLoc[k][0] && j == RedLoc[k][1]) {
							zhanwei = true;
							result = false;
							break;
						}
					}
					if (!zhanwei) {
						RedLoc[Slt][0] = i;
						RedLoc[Slt][1] = j;
						result = true;
					}
				}
			} else if (i == m + 2 && j == n + 2) {
				for (int k = 0; k <= 15; k++) {
					if ((m == RedLoc[k][0] - 1 && n == RedLoc[k][1] - 1)
							|| (m == BlackLoc[k][0] - 1 && n == BlackLoc[k][1] - 1)) {
						beijiao = true;
						result = false;
						break;
					}
				}
				if (!beijiao) {
					for (int k = 0; k <= 15; k++) {
						if (i == BlackLoc[k][0] && j == BlackLoc[k][1]) {
							BlackLoc[k][0] = 9;
							BlackLoc[k][1] = 10;
							RedLoc[Slt][0] = i;
							RedLoc[Slt][1] = j;
							zhanwei = true;
							result = true;
							break;
						} else if (i == RedLoc[k][0] && j == RedLoc[k][1]) {
							zhanwei = true;
							result = false;
							break;
						}
					}
					if (!zhanwei) {
						RedLoc[Slt][0] = i;
						RedLoc[Slt][1] = j;
						result = true;
					}
				}
			} else if (i == m + 2 && j == n - 2) {
				for (int k = 0; k <= 15; k++) {
					if ((m == RedLoc[k][0] - 1 && n == RedLoc[k][1] + 1)
							|| (m == BlackLoc[k][0] - 1 && n == BlackLoc[k][1] + 1)) {
						beijiao = true;
						result = false;
						break;
					}
				}
				if (!beijiao) {
					for (int k = 0; k <= 15; k++) {
						if (i == BlackLoc[k][0] && j == BlackLoc[k][1]) {
							BlackLoc[k][0] = 9;
							BlackLoc[k][1] = 10;
							RedLoc[Slt][0] = i;
							RedLoc[Slt][1] = j;
							zhanwei = true;
							result = true;
							break;
						} else if (i == RedLoc[k][0] && j == RedLoc[k][1]) {
							zhanwei = true;
							result = false;
							break;
						}
					}
					if (!zhanwei) {
						RedLoc[Slt][0] = i;
						RedLoc[Slt][1] = j;
						result = true;
					}
				}
			}// 错误点击，重新点击取消
			else {
				result = false;
			}
		}// 错误点击，重新点击取消
		else {
			result = false;
		}
		return result;
	}

	public boolean rxiangb(int i, int j) {
		if (j <= 4) {
			int m = 0;
			m = BlackLoc[Slt][0];
			int n = 0;
			n = BlackLoc[Slt][1];
			boolean beijiao;
			beijiao = false;
			boolean zhanwei;
			zhanwei = false;
			if (i == m - 2 && j == n - 2) {
				// 判断是否绊腿
				for (int k = 0; k <= 15; k++) {
					if ((m == RedLoc[k][0] + 1 && n == RedLoc[k][1] + 1)
							|| (m == BlackLoc[k][0] + 1 && n == BlackLoc[k][1] + 1)) {
						beijiao = true;
						result = false;
						break;
					}
				}// F
				if (!beijiao) {
					for (int k = 0; k <= 15; k++) {
						if (i == BlackLoc[k][0] && j == BlackLoc[k][1]) {
							zhanwei = true;
							result = false;
							break;
						} else if (i == RedLoc[k][0] && j == RedLoc[k][1]) {
							RedLoc[k][0] = 9;
							RedLoc[k][1] = 10;
							BlackLoc[Slt][0] = i;
							BlackLoc[Slt][1] = j;
							zhanwei = true;
							result = true;
							break;
						}
					}
					if (!zhanwei) {
						BlackLoc[Slt][0] = i;
						BlackLoc[Slt][1] = j;
						result = true;
					}
				}
			} else if (i == m - 2 && j == n + 2) {
				for (int k = 0; k <= 15; k++) {
					if ((m == RedLoc[k][0] + 1 && n == RedLoc[k][1] - 1)
							|| (m == BlackLoc[k][0] + 1 && n == BlackLoc[k][1] - 1)) {
						beijiao = true;
						result = false;
						break;
					}
				}
				if (!beijiao) {
					for (int k = 0; k <= 15; k++) {
						if (i == BlackLoc[k][0] && j == BlackLoc[k][1]) {
							zhanwei = true;
							result = false;
							break;
						} else if (i == RedLoc[k][0] && j == RedLoc[k][1]) {
							RedLoc[k][0] = 9;
							RedLoc[k][1] = 10;
							BlackLoc[Slt][0] = i;
							BlackLoc[Slt][1] = j;
							zhanwei = true;
							result = true;
							break;
						}
					}
					if (!zhanwei) {
						BlackLoc[Slt][0] = i;
						BlackLoc[Slt][1] = j;
						result = true;
					}
				}
			} else if (i == m + 2 && j == n + 2) {
				for (int k = 0; k <= 15; k++) {
					if ((m == RedLoc[k][0] - 1 && n == RedLoc[k][1] - 1)
							|| (m == BlackLoc[k][0] - 1 && n == BlackLoc[k][1] - 1)) {
						beijiao = true;
						result = false;
						break;
					}
				}
				if (!beijiao) {
					for (int k = 0; k <= 15; k++) {
						if (i == BlackLoc[k][0] && j == BlackLoc[k][1]) {
							zhanwei = true;
							result = false;
							break;
						} else if (i == RedLoc[k][0] && j == RedLoc[k][1]) {
							RedLoc[k][0] = 9;
							RedLoc[k][1] = 10;
							BlackLoc[Slt][0] = i;
							BlackLoc[Slt][1] = j;
							zhanwei = true;
							result = true;
							break;
						}
					}
					if (!zhanwei) {
						BlackLoc[Slt][0] = i;
						BlackLoc[Slt][1] = j;
						result = true;
					}
				}
			} else if (i == m + 2 && j == n - 2) {
				for (int k = 0; k <= 15; k++) {
					if ((m == RedLoc[k][0] - 1 && n == RedLoc[k][1] + 1)
							|| (m == BlackLoc[k][0] - 1 && n == BlackLoc[k][1] + 1)) {
						beijiao = true;
						result = false;
						break;
					}
				}
				if (!beijiao) {
					for (int k = 0; k <= 15; k++) {
						if (i == BlackLoc[k][0] && j == BlackLoc[k][1]) {
							zhanwei = true;
							result = false;
							break;
						} else if (i == RedLoc[k][0] && j == RedLoc[k][1]) {
							RedLoc[k][0] = 9;
							RedLoc[k][1] = 10;
							BlackLoc[Slt][0] = i;
							BlackLoc[Slt][1] = j;
							zhanwei = true;
							result = true;
							break;
						}
					}
					if (!zhanwei) {
						BlackLoc[Slt][0] = i;
						BlackLoc[Slt][1] = j;
						result = true;
					}
				}
			}// 错误点击，重新点击取消
			else {
				result = false;
			}
		}// 错误点击，重新点击取消
		else {
			result = false;
		}
		return result;
	}

	public void shi(Canvas canvas, int i) {
		int x = startX + RedLoc[i][0] * buchang;
		int y = startY + RedLoc[i][1] * buchang;

		int m = startX + BlackLoc[i][0] * buchang;
		int n = startY + BlackLoc[i][1] * buchang;
		fugai(canvas, m, n, x, y);
		canvas.drawText("仕", x - buchang * 24 / 100, y + buchang * 23 / 100,
				redPaint);
		canvas.drawText("士", m - buchang * 24 / 100, n + buchang * 23 / 100,
				blackPaint);
	}

	public boolean rshi(int i, int j) {
		int m = 0;
		m = RedLoc[Slt][0];
		int n = 0;
		n = RedLoc[Slt][1];
		boolean zhanwei;
		zhanwei = false;
		if (m == 3 && n == 7) {
			if (i == m + 1 && j == n + 1) {
				for (int k = 0; k <= 15; k++) {
					if (BlackLoc[k][0] == i && BlackLoc[k][1] == j) {
						BlackLoc[k][0] = 9;
						BlackLoc[k][1] = 10;
						RedLoc[Slt][0] = i;
						RedLoc[Slt][1] = j;
						result = true;
						zhanwei = true;
						break;
					} else if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
						result = false;
						zhanwei = true;
						break;
					}
				}
				if (!zhanwei) {
					RedLoc[Slt][0] = i;
					RedLoc[Slt][1] = j;
					result = true;
				}
			}// 错误点击，重新点击取消
			else {
				result = false;
			}
		} else if (m == 3 && n == 9) {
			if (i == m + 1 && j == n - 1) {

				for (int k = 0; k <= 15; k++) {
					if (BlackLoc[k][0] == i && BlackLoc[k][1] == j) {
						BlackLoc[k][0] = 9;
						BlackLoc[k][1] = 10;
						RedLoc[Slt][0] = i;
						RedLoc[Slt][1] = j;
						result = true;
						zhanwei = true;
						break;
					} else if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
						result = false;
						zhanwei = true;
						break;
					}
				}
				if (!zhanwei) {
					RedLoc[Slt][0] = i;
					RedLoc[Slt][1] = j;
					result = true;
				}
			}// 错误点击，重新点击取消
			else {
				result = false;
			}
		} else if (m == 5 && n == 7) {
			if (i == m - 1 && j == n + 1) {

				for (int k = 0; k <= 15; k++) {
					if (BlackLoc[k][0] == i && BlackLoc[k][1] == j) {
						BlackLoc[k][0] = 9;
						BlackLoc[k][1] = 10;
						RedLoc[Slt][0] = i;
						RedLoc[Slt][1] = j;
						result = true;
						zhanwei = true;
						break;
					} else if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
						result = false;
						zhanwei = true;
						break;
					}
				}
				if (!zhanwei) {
					RedLoc[Slt][0] = i;
					RedLoc[Slt][1] = j;
					result = true;
				}
			}// 错误点击，重新点击取消
			else {
				result = false;
			}
		} else if (m == 5 && n == 9) {
			if (i == m - 1 && j == n - 1) {

				for (int k = 0; k <= 15; k++) {
					if (BlackLoc[k][0] == i && BlackLoc[k][1] == j) {
						BlackLoc[k][0] = 9;
						BlackLoc[k][1] = 10;
						RedLoc[Slt][0] = i;
						RedLoc[Slt][1] = j;
						result = true;
						zhanwei = true;
						break;
					} else if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
						result = false;
						zhanwei = true;
						break;
					}
				}
				if (!zhanwei) {
					RedLoc[Slt][0] = i;
					RedLoc[Slt][1] = j;
					result = true;
				}
			}// 错误点击，重新点击取消
			else {
				result = false;
			}
		} else if (m == 4 && n == 8) {
			if ((i + 1 == m && j + 1 == n) || (i - 1 == m && j - 1 == n)
					|| (i + 1 == m && j - 1 == n) || (i - 1 == m && j + 1 == n)) {
				for (int k = 0; k <= 15; k++) {
					if (BlackLoc[k][0] == i && BlackLoc[k][1] == j) {
						BlackLoc[k][0] = 9;
						BlackLoc[k][1] = 10;
						RedLoc[Slt][0] = i;
						RedLoc[Slt][1] = j;
						result = true;
						zhanwei = true;
						break;
					} else if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
						result = false;
						zhanwei = true;
						break;
					}
				}
				if (!zhanwei) {
					RedLoc[Slt][0] = i;
					RedLoc[Slt][1] = j;
					result = true;
				}
			}// 错误点击，重新点击取消
			else {
				result = false;
			}
		}
		return result;
	}

	public boolean rshib(int i, int j) {
		int m = 0;
		m = BlackLoc[Slt][0];
		int n = 0;
		n = BlackLoc[Slt][1];
		boolean zhanwei;
		zhanwei = false;
		if (m == 3 && n == 0) {
			if (i == m + 1 && j == n + 1) {
				for (int k = 0; k <= 15; k++) {
					if (BlackLoc[k][0] == i && BlackLoc[k][1] == j) {
						result = false;
						zhanwei = true;
						break;
					} else if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
						RedLoc[k][0] = 9;
						RedLoc[k][1] = 10;
						BlackLoc[Slt][0] = i;
						BlackLoc[Slt][1] = j;
						result = true;
						zhanwei = true;
						break;
					}
				}
				if (!zhanwei) {
					BlackLoc[Slt][0] = i;
					BlackLoc[Slt][1] = j;
					result = true;
				}
			}// 错误点击，重新点击取消
			else {
				result = false;
			}
		} // F
		else if (m == 3 && n == 2) {
			if (i == m + 1 && j == n - 1) {
				for (int k = 0; k <= 15; k++) {
					if (BlackLoc[k][0] == i && BlackLoc[k][1] == j) {
						result = false;
						zhanwei = true;
						break;
					} else if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
						RedLoc[k][0] = 9;
						RedLoc[k][1] = 10;
						BlackLoc[Slt][0] = i;
						BlackLoc[Slt][1] = j;
						result = true;
						zhanwei = true;
						break;
					}
				}
				if (!zhanwei) {
					BlackLoc[Slt][0] = i;
					BlackLoc[Slt][1] = j;
					result = true;
				}
			}// 错误点击，重新点击取消
			else {
				result = false;
			}
		} //
		else if (m == 5 && n == 0) {
			if (i == m - 1 && j == n + 1) {
				for (int k = 0; k <= 15; k++) {
					if (BlackLoc[k][0] == i && BlackLoc[k][1] == j) {
						result = false;
						zhanwei = true;
						break;
					} else if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
						RedLoc[k][0] = 9;
						RedLoc[k][1] = 10;
						BlackLoc[Slt][0] = i;
						BlackLoc[Slt][1] = j;
						result = true;
						zhanwei = true;
						break;
					}
				}
				if (!zhanwei) {
					BlackLoc[Slt][0] = i;
					BlackLoc[Slt][1] = j;
					result = true;
				}
			}// 错误点击，重新点击取消
			else {
				result = false;
			}
		} //
		else if (m == 5 && n == 2) {
			if (i == m - 1 && j == n - 1) {
				for (int k = 0; k <= 15; k++) {
					if (BlackLoc[k][0] == i && BlackLoc[k][1] == j) {
						result = false;
						zhanwei = true;
						break;
					} else if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
						RedLoc[k][0] = 9;
						RedLoc[k][1] = 10;
						BlackLoc[Slt][0] = i;
						BlackLoc[Slt][1] = j;
						result = true;
						zhanwei = true;
						break;
					}
				}
				if (!zhanwei) {
					BlackLoc[Slt][0] = i;
					BlackLoc[Slt][1] = j;
					result = true;
				}
			}// 错误点击，重新点击取消
			else {
				result = false;
			}
		} else if (m == 4 && n == 1) {
			if ((i + 1 == m && j + 1 == n) || (i - 1 == m && j - 1 == n)
					|| (i + 1 == m && j - 1 == n) || (i - 1 == m && j + 1 == n)) {
				for (int k = 0; k <= 15; k++) {
					if (BlackLoc[k][0] == i && BlackLoc[k][1] == j) {
						result = false;
						zhanwei = true;
						break;
					} else if (RedLoc[k][0] == i && RedLoc[k][1] == j) {
						RedLoc[k][0] = 9;
						RedLoc[k][1] = 10;
						BlackLoc[Slt][0] = i;
						BlackLoc[Slt][1] = j;
						result = true;
						zhanwei = true;
						break;
					}
				}
				if (!zhanwei) {
					BlackLoc[Slt][0] = i;
					BlackLoc[Slt][1] = j;
					result = true;
				}
			}
			// 错误点击，重新点击取消
			else {
				result = false;
			}
		}
		return result;
	}

	// 上
	public int mins(int i) {
		int min = -1;
		if (MoveSide) {
			for (int n = 0; n <= 15; n++) {
				if (RedLoc[n][0] == RedLoc[i][0]) {
					if (RedLoc[n][1] < RedLoc[i][1]) {
						if (min < RedLoc[n][1]) {
							min = RedLoc[n][1];
						}
					}
				}
				if (BlackLoc[n][0] == RedLoc[i][0]) {
					if (BlackLoc[n][1] < RedLoc[i][1]) {
						if (RedLoc[n][0] == RedLoc[i][0]) {// 红色棋子同行
							if (RedLoc[n][1] < RedLoc[i][1]) {
								if (RedLoc[n][1] < BlackLoc[n][1]) {
									if (min < BlackLoc[n][1]) {
										min = BlackLoc[n][1];
									}
								}//

							} else {
								if (min < BlackLoc[n][1]) {
									min = BlackLoc[n][1];
								}
							}
						}//
						else {
							if (min < BlackLoc[n][1]) {
								min = BlackLoc[n][1];
							}
						}
					}
				}
			}
		}// F
		else {
			for (int n = 0; n <= 15; n++) {
				if (RedLoc[n][0] == BlackLoc[i][0]) {
					if (RedLoc[n][1] < BlackLoc[i][1]) {
						if (min < RedLoc[n][1]) {
							min = RedLoc[n][1];
						}
					}
				}
				if (BlackLoc[n][0] == BlackLoc[i][0]) {
					if (BlackLoc[n][1] < BlackLoc[i][1]) {
						if (RedLoc[n][0] == BlackLoc[i][0]) {// 红色棋子同行
							if (RedLoc[n][1] < BlackLoc[i][1]) {
								if (RedLoc[n][1] < BlackLoc[n][1]) {
									if (min < BlackLoc[n][1]) {
										min = BlackLoc[n][1];
									}
								}//

							} else {
								if (min < BlackLoc[n][1]) {
									min = BlackLoc[n][1];
								}
							}
						}//
						else {
							if (min < BlackLoc[n][1]) {
								min = BlackLoc[n][1];
							}
						}
					}
				}
			}
		}
		return min;
	}

	// 下
	public int maxx(int i) {
		int max = 10;
		// 下移
		if (MoveSide) {
			for (int n = 0; n <= 15; n++) {
				if (RedLoc[n][0] == RedLoc[i][0]) {
					if (RedLoc[n][1] > RedLoc[i][1]) {
						if (max > RedLoc[n][1]) {
							max = RedLoc[n][1];
						}
					}
				}
				if (BlackLoc[n][0] == RedLoc[i][0]) {
					if (BlackLoc[n][1] > RedLoc[i][1]) {
						if (RedLoc[n][0] == RedLoc[i][0]) {
							if (RedLoc[n][1] > RedLoc[i][1]) {
								if (RedLoc[n][1] > BlackLoc[n][1]) {
									if (max > BlackLoc[n][1]) {
										max = BlackLoc[n][1];
									}
								}//

							} else {
								if (max > BlackLoc[n][1]) {
									max = BlackLoc[n][1];
								}
							}
						}//
						else {
							if (max > BlackLoc[n][1]) {
								max = BlackLoc[n][1];
							}
						}
					}
				}
			}
		}// F
		else {
			for (int n = 0; n <= 15; n++) {
				if (RedLoc[n][0] == BlackLoc[i][0]) {
					if (RedLoc[n][1] > BlackLoc[i][1]) {
						if (max > RedLoc[n][1]) {
							max = RedLoc[n][1];
						}
					}
				}
				if (BlackLoc[n][0] == BlackLoc[i][0]) {
					if (BlackLoc[n][1] > BlackLoc[i][1]) {
						if (RedLoc[n][0] == BlackLoc[i][0]) {
							if (RedLoc[n][1] > BlackLoc[i][1]) {
								if (RedLoc[n][1] > BlackLoc[n][1]) {
									if (max > BlackLoc[n][1]) {
										max = BlackLoc[n][1];
									}
								}//

							} else {
								if (max > BlackLoc[n][1]) {
									max = BlackLoc[n][1];
								}
							}
						}//
						else {
							if (max > BlackLoc[n][1]) {
								max = BlackLoc[n][1];
							}
						}
					}
				}
			}
		}
		return max;
	}

	// 左移
	public int minz(int i) {
		int min = -1;
		if (MoveSide) {
			for (int n = 0; n <= 15; n++) {
				if (RedLoc[n][1] == RedLoc[i][1]) {
					if (RedLoc[n][0] < RedLoc[i][0]) {
						if (min < RedLoc[n][0]) {
							min = RedLoc[n][0];
						}
					}
				}
				if (BlackLoc[n][1] == RedLoc[i][1]) {
					if (BlackLoc[n][0] < RedLoc[i][0]) {
						if (RedLoc[n][1] == RedLoc[i][1]) {
							if (RedLoc[n][0] < RedLoc[i][0]) {
								if (RedLoc[n][0] < BlackLoc[n][0]) {
									if (min < BlackLoc[n][0]) {
										min = BlackLoc[n][0];
									}
								}//

							} else {
								if (min < BlackLoc[n][0]) {
									min = BlackLoc[n][0];
								}
							}
						}//
						else {
							if (min < BlackLoc[n][0]) {
								min = BlackLoc[n][0];
							}
						}

					}
				}
			}
		}// F
		else {
			for (int n = 0; n <= 15; n++) {
				if (RedLoc[n][1] == BlackLoc[i][1]) {
					if (RedLoc[n][0] < BlackLoc[i][0]) {
						if (min < RedLoc[n][0]) {
							min = RedLoc[n][0];
						}
					}
				}
				if (BlackLoc[n][1] == BlackLoc[i][1]) {
					if (BlackLoc[n][0] < BlackLoc[i][0]) {
						if (RedLoc[n][1] == BlackLoc[i][1]) {
							if (RedLoc[n][0] < BlackLoc[i][0]) {
								if (RedLoc[n][0] < BlackLoc[n][0]) {
									if (min < BlackLoc[n][0]) {
										min = BlackLoc[n][0];
									}
								}//

							} else {
								if (min < BlackLoc[n][0]) {
									min = BlackLoc[n][0];
								}
							}
						}//
						else {
							if (min < BlackLoc[n][0]) {
								min = BlackLoc[n][0];
							}
						}

					}
				}
			}
		}
		return min;
	}

	// 右移
	public int maxy(int i) {
		int max = 9;
		if (MoveSide) {
			for (int n = 0; n <= 15; n++) {
				if (RedLoc[n][1] == RedLoc[i][1]) {
					if (RedLoc[n][0] > RedLoc[i][0]) {
						if (max > RedLoc[n][0]) {
							max = RedLoc[n][0];
						}
					}
				}
				if (BlackLoc[n][1] == RedLoc[i][1]) {
					if (BlackLoc[n][0] > RedLoc[i][0]) {
						if (RedLoc[n][1] == RedLoc[i][1]) {
							if (RedLoc[n][0] > RedLoc[i][0]) {
								if (RedLoc[n][0] > BlackLoc[n][0]) {
									if (max > BlackLoc[n][0]) {
										max = BlackLoc[n][0];
									}
								}//

							} else {
								if (max > BlackLoc[n][0]) {
									max = BlackLoc[n][0];
								}
							}
						}//
						else {
							if (max > BlackLoc[n][0]) {
								max = BlackLoc[n][0];
							}
						}
					}
				}
			}
		}// F
		else {
			for (int n = 0; n <= 15; n++) {
				if (RedLoc[n][1] == BlackLoc[i][1]) {
					if (RedLoc[n][0] > BlackLoc[i][0]) {
						if (max > RedLoc[n][0]) {
							max = RedLoc[n][0];
						}
					}
				}
				if (BlackLoc[n][1] == BlackLoc[i][1]) {
					if (BlackLoc[n][0] > BlackLoc[i][0]) {
						if (RedLoc[n][1] == BlackLoc[i][1]) {
							if (RedLoc[n][0] > BlackLoc[i][0]) {
								if (RedLoc[n][0] > BlackLoc[n][0]) {
									if (max > BlackLoc[n][0]) {
										max = BlackLoc[n][0];
									}
								}//

							} else {
								if (max > BlackLoc[n][0]) {
									max = BlackLoc[n][0];
								}
							}
						}//
						else {
							if (max > BlackLoc[n][0]) {
								max = BlackLoc[n][0];
							}
						}
					}
				}
			}
		}
		return max;
	}

	public boolean jiangjun() {
		return jz;
	}
}
