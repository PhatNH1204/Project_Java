import 'package:flutter_test/flutter_test.dart';
import 'package:smart_heritage/app/app.dart';

void main() {
  testWidgets('Smart Heritage app starts', (tester) async {
    await tester.pumpWidget(const SmartHeritageApp());

    expect(find.text('Smart Heritage'), findsOneWidget);
  });
}
