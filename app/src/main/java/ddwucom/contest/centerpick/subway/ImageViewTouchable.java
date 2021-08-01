package ddwucom.contest.centerpick.subway;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import ddwucom.contest.centerpick.R;

//멀티 터치(pinch-zoom)이 가능하게 해주는 class
//특정 영역을 터치했을 때 해당영역이 어떤 영역인지 반환
public class ImageViewTouchable extends androidx.appcompat.widget.AppCompatImageView {
    //MainActivity MainActivity;

    static String clickStationName;
    private Context context;
    //해당 기기의 밀도구하는 공식
    float density = getResources().getDisplayMetrics().density / 3.5f;
    private float minimumScale = 1.9535f;
    private float maximumScale = 5.0f;
    private float scale = 5.0f;
    private float xPointer[] = new float[2];
    private float yPointer[] = new float[2];
    private float movementX = 0;
    private float movementY = 0;

    float imgWidth = MainActivity2.imgSizeWidth;
    float imgHeight = MainActivity2.imgSizeHeight;
    float imgSizeGetYHeight = MainActivity2.imgSizeHeightGetYHeight;
    float HeightForStatusSoft = MainActivity2.statusBarHeight + MainActivity2.softKeyHeight;
    float modelFactor = 1f;
    private float initX = imgWidth / 2;
    private float initY = imgHeight / 2;
    private boolean moveDetect = false;
    private double firstDist, secondDist, secondMinusFirstDist;
    float realImageHeight;

    public ImageViewTouchable(final Context context) {
        super(context);
        super.setClickable(true);
        this.context = context;
        //여백을 제외한 실제이미지 높이
        realImageHeight = 1952 * this.imgWidth / 2408;
        setScaleX(maximumScale);
        setScaleY(maximumScale);
        setPivotX(initX);
        setPivotY(initY);

        if(Build.MODEL.contains("LG")){
            HeightForStatusSoft = MainActivity2.statusBarHeight + MainActivity2.softKeyHeight;
        }else if(Build.MODEL.contains("SM")){
            HeightForStatusSoft = 0;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            //터치 하나를 다운했을때
            case MotionEvent.ACTION_DOWN:
                if (event.getPointerCount() < 2 && event.getActionIndex() < 2) {  //getPointerCount:터치 개수
                    //getPointerId:터치한 순간부터 주어지는 고유번호, getActionIndex:비트에 대한 도우미상수
                    xPointer[event.getPointerId(event.getActionIndex())] = event.getX(event.getPointerId(event.getActionIndex()));
                    yPointer[event.getPointerId(event.getActionIndex())] = event.getY(event.getPointerId(event.getActionIndex()));
                    moveDetect = true;
                    Log.v("DOWNTOUCH", "(" + event.getX() + "," + event.getY() + ")");
                }
                break;

            //터치 두개 이상을 다운했을때 발생
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() < 3) {
                    xPointer[event.getPointerId(event.getActionIndex())] = event.getX(event.getPointerId(event.getActionIndex()));
                    yPointer[event.getPointerId(event.getActionIndex())] = event.getY(event.getPointerId(event.getActionIndex()));
                    //두번째 손가락을 짚는 순간 첫번째 거리가 정해짐
                    firstDist = Math.sqrt(Math.pow(xPointer[0] - xPointer[1], 2) + Math.pow(yPointer[0] - yPointer[1], 2));
                    Log.v("2_DOWNTOUCH", "(" + event.getX() + "," + event.getY() + ")");
                }
                //ACTION_DOWN 후 move가 변길이 3사각형을 벗어나면 xml파싱하지않고, intent도 안넘김

                //터치가 움직일때(터치 중)
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1) {
                    //x가 증가하는 것은 중심점이 x방향으로 가고있는 것
                    movementX = movementX - (xPointer[0] - (event.getX()));     //터치한 x좌표
                    movementY = movementY - (yPointer[0] - (event.getY()));
                    setPivotX(initX - movementX);

                    if (initY - movementY >= initY + realImageHeight / 2) { //y좌표가 중간점보다 높을때(아마?ㅎ)
                        setPivotY(initY + realImageHeight / 2);
                        movementY = -realImageHeight / 2;
                    } else if (initY - movementY <= initY - realImageHeight / 2) {  //y좌표가 중간점보다 낮을때
                        setPivotY(initY - realImageHeight / 2);
                        movementY = realImageHeight / 2;
                    } else {                                            //이건뭐야 안움직엿다는 거같은데 아닌가 y만 움직엿다는건가 엥
                        setPivotY(initY - movementY);
                    }

                    if (initX - movementX >= initX + initX) {
                        setPivotX(initX + initX);
                        movementX = -initX;
                    } else if (initX - movementX <= initX - initX) {
                        setPivotX(initX - initX);
                        movementX = initX;
                    } else {
                        setPivotX(initX - movementX);
                    }

                    if ((event.getX()) >= xPointer[0] + 3 || (event.getX()) <= xPointer[0] - 3 || (event.getY()) >= yPointer[0] + 3 || (event.getY()) <= yPointer[0] - 3)
                        moveDetect = false;
                } else if (event.getPointerCount() == 2) {
                    try {
                        secondDist = Math.sqrt(Math.pow((event.getX(event.getPointerId(0))) - (event.getX(event.getPointerId(1))), 2)
                                + Math.pow((event.getY(event.getPointerId(0))) - (event.getY(event.getPointerId(1))), 2));
                        secondMinusFirstDist = secondDist - firstDist;
                        //scale에 length/firstDist 값(거리변화값)이 지속적으로 누적되는 형태
                        scale = (float) (scale + secondMinusFirstDist / firstDist);
                        if (scale >= maximumScale) {
                            scale = maximumScale;
                        } else if (scale <= minimumScale) {
                            scale = minimumScale;
                        }
                        setScaleX(scale);
                        setScaleY(scale);
                    } catch (IllegalArgumentException e) {
                        Log.v("TOUCHMOVE Exception", "IllegalArguemntException!!");
                    }
                } else {
                    break;
                }
                break;

            //터치 하나에서 업할때(터치 종료)
            case MotionEvent.ACTION_UP:
                final float RANGE = 10 * density;
                //한개 손가락으로 moveDetect가 true일 경우(움직임 발견)
                if (moveDetect && event.getPointerCount() == 1) {
                    String startTag = "";
                    int eventType;
                    float x = 0;
                    float y = 0;
                    String stationName = null;
                    //이 부분은 pointers.xml에서 선택한 역이 어떤 역인지 알려주는 역할
                    //어떤 역인지 저장할것. 만약 몇호선인지까지 저장하면 환승역이 골치아파짐
                    try {
                        // x, y, stationName은 하나의 station 종료태그를 지날때마다 초기화해야함.
                        XmlResourceParser pointers = getResources().getXml(R.xml.pointers);
                        breakOut:
                        while ((eventType = pointers.getEventType()) != XmlResourceParser.END_DOCUMENT) {
                            switch (eventType) {
                                case XmlResourceParser.START_DOCUMENT:
                                    break;

                                case XmlResourceParser.START_TAG:
                                    startTag = pointers.getName();
                                    break;

                                case XmlResourceParser.TEXT:
                                    float xFactor = ((event.getX() - imgWidth / 2) / (imgWidth / 2)) * (1440 - imgWidth);
                                    //float yFactor = ((event.getY() - imgHeight/2) / (imgHeight / 2)*( 2280 - imgHeight - HeightForStatusSoft) )*modelFactor;
                                    float yFactor = ((event.getY() - imgHeight / 2) / (imgHeight / 2) * (2280 - imgHeight - HeightForStatusSoft));
                                    Log.v("currentModelFactor", String.valueOf(modelFactor));
                                    switch (startTag) {
                                        case "x":
                                            x = Float.parseFloat(pointers.getText()) - (1440 - imgWidth + xFactor) / 2;
                                            Log.v("X-imgWidth", "" + (event.getX() - imgWidth / 2));
                                            Log.v("value", "" + xFactor);
                                            Log.v("XML파싱_DOWNTOUCH", "(" + event.getX() + "," + event.getY() + ")");
                                            break;
                                        case "y":
                                            y = Float.parseFloat(pointers.getText()) - (2280 - imgHeight + yFactor) / 2;
                                            Log.v("imgHeight", "" + imgHeight);
                                            Log.v("imgSizeGetYHeight", "" + imgSizeGetYHeight);
                                            Log.v("realImageHeight", "" + realImageHeight);
                                            break;
                                        case "name":
                                            stationName = pointers.getText();
                                            break;
                                    }// START_TAG에서 저장되었던 startTag가 x, y, name를 판별해 그 값들을 일일이 저장해서 END_TAG 부분에서 터치한 좌표값이 해당 범위 내에 있는지 확인
                                    break;

                                case XmlResourceParser.END_TAG:
                                    if (pointers.getName().equals("station") && (event.getX()) >= x - RANGE && (event.getX()) <= x + RANGE &&
                                            (event.getY()) <= y + RANGE && (event.getY()) >= y - RANGE) { // 종료태그가 station이면
                                        // 종료태그가 station이고 터치한 좌표값이 위의 XmlResourceParser.TEXT의 case에서 저장한 범위 안에 있으면,
                                        // 이에 해당하는 역명을 출력한다.
//                                        Toast.makeText(context, stationName, Toast.LENGTH_SHORT).show();

                                        ((MainActivity2) context).setFBVisibility(View.VISIBLE);   //내용도 보이고 공간도 차지한다.
                                        ((MainActivity2) context).setFBText(stationName);
                                        clickStationName = stationName;
                                        //뷰에 text설정
                                        //((MainActivity)context).sendIntent(stationName);
                                        //startActivity 메서드는 이 클래스(ImageViewTouchable)에서는 호출할 수 없기때문에 이 방법을 사용함.
                                        break breakOut;
                                    } else {
                                        ((MainActivity2) context).setFBVisibility(View.INVISIBLE);   //내용은 보이지 않지만 공간은 차지한다.
                                    }
                                    break;
                            }
                            pointers.next();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        invalidate();
        return super.onTouchEvent(event);
    }
}
