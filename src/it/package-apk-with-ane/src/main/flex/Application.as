package {

import flash.display.Sprite;

import com.adobe.nativeExtensions.Vibration;

public class Application extends Sprite {

    public function Application() {

        if (Vibration.isSupported)
        {
            var vibe:Vibration = new Vibration();
            vibe.vibrate(2000);
        }
    }
}

}