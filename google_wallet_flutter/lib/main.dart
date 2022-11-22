import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_svg/svg.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  final addToWallet =
      const MethodChannel('com.example.google_wallet_flutter/google_wallet');

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            GestureDetector(
              onTap: () {
                _addToWallet();
                showToast();
                //do flutter channel job here
              },
              child: Stack(
                children: [
                  Container(
                    width: double.infinity,
                    margin: EdgeInsets.symmetric(horizontal: 20),
                    height: 50,
                    decoration: BoxDecoration(
                      color: Colors.black,
                      borderRadius: BorderRadius.circular(25),
                      boxShadow: [
                        BoxShadow(
                          color: Colors.grey.withOpacity(0.5),
                          spreadRadius: 5,
                          blurRadius: 7,
                          offset:
                              const Offset(0, 3), // changes position of shadow
                        ),
                      ],
                    ),
                  ),
                  SvgPicture.asset(
                    'assets/images/add_to_google_wallet_button_content.svg',
                    width: double.infinity,
                    height: 50,
                    fit: BoxFit.scaleDown,
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
      // This trailing comma makes auto-formatting nicer for build methods.
    );
  }

  void showToast() {
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        content: Text('Yay! A SnackBar!'),
      ),
    );
  }

  //call to native code
  Future _addToWallet() async {
    try {
      final String result = await addToWallet.invokeMethod('addToWallet');
      print(result);
    } on PlatformException catch (e) {
      print(e.message);
    }
  }
}
