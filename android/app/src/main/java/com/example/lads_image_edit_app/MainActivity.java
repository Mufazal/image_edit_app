package com.example.lads_image_edit_app;

import java.io.File;

import io.flutter.embedding.android.FlutterActivity;

public class MainActivity extends FlutterActivity {

    File imageFile;
    private static final String CHANNEL = "sample.resolution.dev/image";

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(),CHANNEL)
                .setMethodCallHandler((call, result) -> {
                    if (call.method.equals("getModifiedImage")){

                        String image = call.argument("image");
                        if (image != null){
                            imageFile= new File(image);
                            System.out.print(image);
                            result.success(image);
                        }


                    }
                    else{
                        result.notImplemented();
                    }
                });
    }

    String getImage(){
        return "HI";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    void showDialog(){


    }
}

