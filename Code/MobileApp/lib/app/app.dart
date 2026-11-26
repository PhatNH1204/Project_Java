import 'package:material_ui/material_ui.dart';

import '../core/theme/app_theme.dart';

class SmartHeritageApp extends StatelessWidget {
  const SmartHeritageApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Smart Heritage',
      theme: AppTheme.lightTheme,
      home: const Scaffold(body: Center(child: Text('Smart Heritage'))),
    );
  }
}
