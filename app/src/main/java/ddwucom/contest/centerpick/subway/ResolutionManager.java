package ddwucom.contest.centerpick.subway;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.RelativeLayout;

import ddwucom.contest.centerpick.R;

public class ResolutionManager {
    float xWidth, yHeight; //단말기의 디스플레이(스크린)사이즈
    float softKeyHeight = 0; //소프트키의 사이즈
    float statusBarHeight = 0;
    float searchBarHeight = 0;
    Activity activity;

    public ResolutionManager(Activity activity){
        this.activity = activity;

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        xWidth = size.x;
        yHeight = size.y;
        Log.v("SIZE:DISPLAY",xWidth+","+yHeight);

        Resources res = activity.getResources();
        int resourceId = res.getIdentifier("navigation_bar_height","dimen","android");
        if(resourceId > 0){
            softKeyHeight = res.getDimensionPixelSize(resourceId);
        }
        Log.v("SIZE:SOFTKEY",String.valueOf(softKeyHeight));

        Rect rectgle = new Rect();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
        statusBarHeight = rectgle.top;
        Log.v("SIZE:STATUSBAR",String.valueOf(statusBarHeight));
        // 단 이거는 onWindowsFocusChanged에서 실행할것..

        RelativeLayout searchBar = (RelativeLayout)activity.findViewById(R.id.RelativeLayout);
        searchBarHeight = searchBar.getHeight();
    }

    public float getxWidth(){
        return xWidth;
    }

    public float getyHeight(){
        return yHeight;
    }

    public float getSoftKeyHeight(){
        return softKeyHeight;
    }

    public float getStatusBarHeight(){
        return statusBarHeight;
    }

    public float getSearchBarHeight(){
        return searchBarHeight;
    }

    public float getImageViewSize(){
        if(isLGPhone() == false) {
            Log.i("phoneType", "Not LGPhone");
            return getyHeight() - getStatusBarHeight() - getSearchBarHeight();
        }else if(isLGPhone() == true){
            Log.i("phoneType", "LGPhone");
            return getyHeight() - getStatusBarHeight() - getSearchBarHeight() - getSoftKeyHeight(); // 맞을지 확인하기.
        }else{
            return -1;
        }
    }

    public boolean isLGPhone(){
        //메뉴버튼 존재유무
        boolean hasMenuKey = ViewConfiguration.get(activity).hasPermanentMenuKey();
        //뒤로가기버튼 존재유무
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        if (!hasMenuKey && !hasBackKey) { // lg폰 소프트키일 경우
            Log.i("phoneType", "LGPhone");
            return true;
        } else { // 삼성폰 등.. 메뉴 버튼, 뒤로가기 버튼 존재
            Log.i("phoneType", "Not LGPhone");
            return false;
        }
    }

}
