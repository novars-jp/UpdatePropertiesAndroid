# UpdatePropertiesAndroid

## 概要

- [MaBeeeAndroidSDK](https://github.com/novars-jp/MaBeeeAndroidSDK)をつかって、MaBeeeデバイスのプロパティを更新するサンプルプロジェクトです。

## ライブラリのインポート

AndroidStudioで[build.gradle(Module: app)](https://github.com/novars-jp/FirstMaBeeeAndroid/blob/master/app/build.gradle)に以下を追加します。

```gradle
repositories {
    maven { url 'http://raw.github.com/novars-jp/MaBeeeAndroidSDK/master/repository/' }
}
dependencies {
    compile 'jp.novars.mabeee.sdk:sdk:1.1'
}
```

## ソースの編集

### 1. activity_main.xmlのレイアウト編集

- Scanボタン、Updateボタン、TextViewを持つレイアウトを作成します。

- [activity_main.xml](https://github.com/novars-jp/UpdatePropertiesAndroid/blob/master/app/src/main/res/layout/activity_main.xml)

### 2. ライブラリの初期化

- [MainActivity.java](https://github.com/novars-jp/FirstMaBeeeAndroid/blob/master/app/src/main/java/jp/novars/firstmabeee/MainActivity.java)のonCreateに以下のソース追加します。

```java
App.getInstance().initializeApp(getApplicationContext());
```

- [Appクラス](http://developer.novars.jp/mabeee/android/javadoc/jp/novars/mabeee/sdk/App.html)はこのSDKの中心となるクラスで、Android端末のBluetoothや接続済みのMaBeeeデバイスを管理します。
- App.getInstance()でAppクラスのSingletonインスタンスを取得します。
- AppクラスのinitializeApp(Context context)関数で、Appクラス、ライブラリの初期化を行ないます。
 - これは一般的にはApplicationクラスで最初に呼び出すか、複数回呼び出しても問題ないので、最初のActivityのonCreateなどで呼び出してください。


### 3. Scanボタンのイベント編集

- ScanボタンにOnClickListenerを設定し、ScanAcitivtyを呼び出す処理を追加します。

```java
Button scanButton = (Button)findViewById(R.id.scanButton);
scanButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
       Intent intent = new Intent(MainActivity.this, ScanActivity.class);
       startActivity(intent);
    }
 });
```

- ScanActivityはMaBeeeデバイスのスキャンと接続を簡単に実装できるUIを提供します。


### 4. Updateボタンのイベント編集

- UpdateボタンにOnClickListenerを実装し、MaBeeeデバイスのプロパティをアップデートする処理を追加します。

```java
Button updateButton = (Button)findViewById(R.id.updateButton);
updateButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Device[] devices = App.getInstance().getDevices();
        for (Device device : devices) {
            device.updateRssi();
            device.updateBatteryVoltage();
        }
    }
});
```

- App.getInstance().getDevices()で接続済みのMaBeeeデバイスの配列が取得できます。
- DeviceクラスのupdateRssi()でRSSI（電波受信強度）をアップデートするように指示します。
- DeviceクラスのupdateBatteryVoltate()でMaBeeeデバイスにセットされている電池の電圧をアップデートするように指示します。


### 5. アップデートを受け取るBroadcastReceiverの編集

- DeviceクラスのupdateRssi(), updateBatteryVoltage()でプロパティの値がアップデートされると、Broadcastで通知されます。
- Broadcastを受け取って、画面にデバイスのプロパティを表示するレシーバーを追加します。
- onResume, onPauseでそれらをAppクラスのインスタンスに登録します。

```java
private BroadcastReceiver mReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (App.MABEEE_RSSI_DID_UPDATE_NOTIFICATION.equals(intent.getAction())) {
            Long identifier = intent.getLongExtra("Identifier", 0);
            Device device = App.getInstance().getDevice(identifier);
            mText += "RSSI " + device.getRssi() + "\n";
            mTextView.setText(mText);
            return;
        }
        if (App.MABEEE_BATTERY_VOLTAGE_DID_UPDATE_NOTIFICATION.equals(intent.getAction())) {
            Long identifier = intent.getLongExtra("Identifier", 0);
            Device device = App.getInstance().getDevice(identifier);
            mText += "Battery Voltage " + device.getBatteryVoltage() + "\n";
            mTextView.setText(mText);
            return;
        }
    }
};

@Override
protected void onResume() {
    super.onResume();
    App.getInstance().registerBroadcastReceiver(mReceiver);
}

@Override
protected void onPause() {
    super.onPause();
    App.getInstance().unregisterBroadcastReceiver(mReceiver);
}
```

- App.getInstance().registerBroadcastReceiver()でレシーバーを登録します。
- App.getInstance().unregisterBroadcastReceiver()でレシーバーを登録を解除します。
- App.MABEEE_RSSI_DID_UPDATE_NOTIFICATIONがRSSIがアップデートされたときのActionになります。
- App.MABEEE_BATTERY_VOLTAGE_DID_UPDATE_NOTIFICATIONが電池電圧がアップデートしたときのActionになります。
- どちらもintent.getLongExtra("Identifier", 0)で、MaBeeeデバイスのIDを取得しています。
- App.getInstance().getDevice(identifier)でIDに紐づくMaBeeeデバイスを取得できます。


## 実行

### スキャン実行

- ビルドして実行します。
- AndroidのBluetoothがONになっているかを確認してください。
- MaBeeeをおもちゃなどにセットして、おもちゃなどの電源をONにしてください。
- スキャンボタンを押すと、ScanActivityが表示されます。
- セルには、MaBeeeの名前、RSSI、接続しているかどうかのアイコンが表示されます。

### 接続

- セルをタップすると接続します。
- 接続すると赤いアイコンになります。

### プロパティの更新

- Updateボタンを押すと、画面に更新された値が表示されます。
