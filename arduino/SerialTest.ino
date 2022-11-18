#include <LiquidCrystal.h>

LiquidCrystal lcd(12, 11, 5, 4, 3, 2);

String serialInput = "";
unsigned long prevTime = 0;
int count = 0;

void setup() {
  // put your setup code here, to run once:
  lcd.begin(16,2);
  lcd.clear();
  
  Serial.begin(9600);
  while(!Serial) {
    ;
  }

  lcd.setCursor(0,0);
  lcd.print("Init done");

  lcd.setCursor(0,0);
  lcd.print("Searching . . .");
  
  boolean waiting = true; // Wait for software to ping SerialTest
  while(waiting) {
    
    if (Serial.available()) { // Returns true if Serial.available() > 0     
      serialInput = Serial.readStringUntil('\r'); // Reads string until carriage return character
      lcd.clear();
      lcd.setCursor(0,0);
      lcd.print(serialInput);
      
      if (serialInput.equals("?SerialTest")) {
        Serial.println("Connected");
        waiting = false;
      }
      else {
        Serial.println("!Connected"); // I dunno
      }
      
      String tempString = Serial.readString();
    }
  }
}

void loop() {
  // put your main code here, to run repeatedly:
  delay(1);
  
  if (Serial.available() && count < 5) {
    byte incomingByte = Serial.read();
    if (incomingByte != -1) {             // Doesn't print if error reading
      /*Serial.print("I received: ");
      Serial.println(incomingByte);*/
      lcd.setCursor(0,0);
      lcd.print("I received: ");
      lcd.setCursor(0,1);
      lcd.print(incomingByte);
      if (count == 4) {
        delay(500);
        lcd.clear();
        prevTime = millis();
      }
      count++;
    }
  }
  else if (count >= 5 && count < 20 ){
    lcd.setCursor(0,0);
    Serial.print("> laserN: ");
    Serial.println(millis() - prevTime);
    prevTime = millis();
    lcd.print("Sent");
    count ++;
  }
  else if (count >= 20) {
    
    if (count < 30) {
      delay(500);
      String str1 = "> laser1: ";
      Serial.print(str1);
      lcd.setCursor(0, 0);
      lcd.print("Sent ");
      lcd.print(str1);
      Serial.println(millis() - prevTime);
    }
    else if (count < 40) {
      delay(1000);
      Serial.print("> laser2: ");
      Serial.println(millis() - prevTime);
    }
    else if (count < 45 ) {
      delay(1500);
      Serial.print("> laser3: ");
      Serial.println(millis() - prevTime);
    }
    prevTime = millis();
    count++;
  }
  
}
