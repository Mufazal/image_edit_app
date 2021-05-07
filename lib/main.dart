import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:image_picker/image_picker.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const platform = const MethodChannel("sample.resolution.dev/image");

  List _outputs;

  File _image;
  File _imageModified;
  bool _loading = false;

  ImagePicker _imagePicker = ImagePicker();
  var height, width;

  _getImageFromNative(File image) async {
    print(image.path);
    try {
      var result = await platform
          .invokeMethod('getModifiedImage', {"image": image.path});
      print(result);
    } on PlatformException catch (e) {
      return print("failed to get battery level : '${e.message}'.");
    }
  }

  Future pickImage() async {
    var image = await _imagePicker.getImage(source: ImageSource.gallery);
    //if (image == null) return null;
    //
    if (image != null) {
      setState(() {
        _image = File(image.path);
      });
    }
  }

  @override
  void initState() {
    _loading = true;
    // loadModel().then((value) {});

    // TODO: implement initState
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    height = MediaQuery.of(context).size.height;
    width = MediaQuery.of(context).size.width;
    return Scaffold(
      appBar: AppBar(
        centerTitle: true,
        title: Text(
          "Tensorflow Lite",
          style: TextStyle(color: Colors.white, fontSize: 25),
        ),
        backgroundColor: Colors.blue,
        elevation: 0,
      ),
      body: Container(
        height: height,
        width: width,
        color: Colors.white,
        child: Container(
          //  margin: EdgeInsets.all(20),
          height: height,
          width: MediaQuery.of(context).size.width,
          child: ListView(
            children: <Widget>[
              _image == null ? Container() : Image.file(_image),
              SizedBox(
                height: 20,
              ),
              _image == null ? Container() : Image.file(_image),
              SizedBox(
                height: 20,
              ),
              _image == null
                  ? Container()
                  : _outputs != null
                  ? Text(
                _outputs[0]["label"],
                style: TextStyle(color: Colors.black, fontSize: 20),
              )
                  : Container(child: Text(""))
            ],
          ),
        ),
      ),
      // floatingActionButton: FloatingActionButton(
      //   tooltip: 'Pick Image',
      //   onPressed: pickImage,
      //   child: Icon(
      //     Icons.add_a_photo,
      //     size: 20,
      //     color: Colors.white,
      //   ),
      //   backgroundColor: Colors.amber,
      // ),
      persistentFooterButtons: [
        FloatingActionButton(
          tooltip: 'Pick Image',
          onPressed: pickImage,
          child: Icon(
            Icons.add_a_photo,
            size: 20,
            color: Colors.white,
          ),
          backgroundColor: Colors.amber,
        ),
        FloatingActionButton(
          tooltip: 'Classify Image',
          onPressed: () {
            _getImageFromNative(_image);
          },
          child: Icon(
            Icons.check,
            size: 20,
            color: Colors.white,
          ),
          backgroundColor: Colors.amber,
        ),
      ],
    );
  }
}
