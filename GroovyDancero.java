package com.javacodegeeks.androidaccelerometerexample;
        import android.app.Activity;
        import android.content.Context;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.os.Bundle;
        import android.os.Vibrator;
        import android.os.Environment;
        import android.util.Log;
        import android.widget.TextView;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.Button;
        import android.widget.Toast;
        import java.io.BufferedReader;
        import java.io.BufferedWriter;
        import java.io.File;
        //import java.io.FileInputStream;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        //import java.io.FileReader;
        import java.io.FileWriter;
        import java.util.ArrayList;
        import java.io.IOException;
//Button with button permissions added
// find file path and investigate csv
// when click, save coord, last to file
// uses coordX and lastx ...

public class GroovyDancero extends Activity implements SensorEventListener, OnClickListener{
    //private final String filepath = "/mnt/sdcard/acc.txt";
    private BufferedWriter mBufferedWriter;
    private BufferedReader mBufferedReader;
    //File root, dir, sensorFile, writer;
    //FileOutputStream fOut;
    private String acc;
    private String read_str = "";
    private final String filepath = "/SDCARD/acc.txt";
    // ObjectOutputStream myOutWriter;
   // private Sensor mAccelerometer;

    private FileWriter writer;
    private Button btnStart,btnSave,btnStop; // btnStart, btnStop;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean started = false;
    private ArrayList sensorData;
    //private ArrayList<AccelLocData> sensorData;
    //private String provider;
    private float lastX, lastY, lastZ;
    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;
    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;
    private float vibrateThreshold = 0;
    private TextView currentX, currentY, currentZ, maxX, maxY, maxZ, coordX, coordY, coordZ; //added coords
    public Vibrator v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
                                                                                                    //SAFE1
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_accelerometer_example);
        initializeViews();
                                                                                                    //SAFE1
        //root = android.os.Environment.getExternalStorageDirectory();
        //dir = new File(root.getAbsolutePath() + "/Documents");
        //dir.mkdirs();

        //File oldFile = new File(dir, "data.txt");

        //boolean deleted = oldFile.delete();
        //System.out.println("Delete status = " + deleted);
        //sensorFile = new File(dir, "data.txt");
                                                                                                    //SAFE2
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            vibrateThreshold = accelerometer.getMaximumRange() / 2;
        } else {
            // fai! we dont have an accelerometer!
        }
        //initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                                                                                                    //SAFE2
        sensorData = new ArrayList();
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);



    }


    @Override                                                                                       //SAFE3
    public void onSensorChanged(SensorEvent event) {

        // clean current values
        displayCleanValues();
        // display the current x,y,z accelerometer values
        displayCurrentValues();
        // display the max x,y,z accelerometer values
        displayMaxValues();
        //display last value
        displayLastValues(); //added - real time acc

        // get the change of the x,y,z values of the accelerometer
        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);
        long timestamp = System.currentTimeMillis();

        // if the change is below 2, it is just plain noise //adjust values for sensitivity
        if (deltaX < 2)
            deltaX = 0;
        if (deltaY < 2)
            deltaY = 0;
        if ((deltaX > vibrateThreshold) || (deltaY > vibrateThreshold) || (deltaZ > vibrateThreshold)) {
            v.vibrate(50);

            // set the last know values of x,y,z
            // these values get written to file since this is accelero
            lastX = event.values[0];
            lastY = event.values[1];
            lastZ = event.values[2];                                                                //SAFE3
         //   AccDataGet data = new AccDataGet(timestamp, lastX, lastY, lastZ);
           // sensorData.add(data);
          //  writer.write(lastX+","+lastY+","+lastZ+"\n");
             acc= String.valueOf(lastX) + ", " + String.valueOf(lastY) + ", " + String.valueOf(lastZ);

            try {

                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/downloads");
                Boolean dirsMade = dir.mkdir();
                //System.out.println(dirsMade);
                Log.v("Accel", dirsMade.toString());

                File file = new File(dir, "output.csv");
                FileOutputStream f = new FileOutputStream(file, true);

            } catch (IOException ex) {
                Toast.makeText(GroovyDancero.this,"....", Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }

        }
    }
   // public void CreateFile(String path)
    //{
      //  File f = new File(path);
        //try {
          //  Log.d("ACTIVITY", "Create a File.");
            //f.createNewFile();
        //} catch (IOException e) {
            // TODO Auto-generated catch block
          //  e.printStackTrace();
       // }
   // }


    public void WriteFile(String filepath, String acc)
    {
        mBufferedWriter = null;
        Toast.makeText(GroovyDancero.this,"WriteTest", Toast.LENGTH_LONG).show();

      //  if (!FileIsExist(filepath))
        //    CreateFile(filepath);

        try
        {
            mBufferedWriter = new BufferedWriter(new FileWriter(filepath, true));
            mBufferedWriter.write(acc);
            mBufferedWriter.newLine();
            mBufferedWriter.flush();
            mBufferedWriter.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
          //  Toast.makeText(GroovyDancero.this,"Failure to Write!!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

//    public boolean FileIsExist(String filepath)
  //  {
    //    File f = new File(filepath);

    //    if (! f.exists())
      //  {
        //    Log.e("ACTIVITY", "File does not exist.");
          //  return false;
        //}
        //else
          //  return true;
    //}

                                                                                                    //SAFE 33

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        // writer = new FileWriter("mydata.txt",true);

    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        // if (writer != null) {
        //   try {
        //     writer.close();
        //} catch (IOException e) {
        // TODO Auto-generated catch block
        //  e.printStackTrace();
        //}
    }                                                                                               //SAFE33

                                                                                                    //SAFE4
// on click -> saveID, R.id.btnSave, R.id.coordX, lastX and the sort
@Override
public void onClick(View v) {
    switch (v.getId()) {
        case R.id.btnStart:
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
           // sensorData = new ArrayList();
            Toast.makeText(GroovyDancero.this,"Test!!!", Toast.LENGTH_LONG).show();
            System.out.println("Start test");
            WriteFile(filepath,acc);
            started = true;
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            break;
        case R.id.btnStop:
            try {
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                // btnUpload.setEnabled(true);
                started = false;
                sensorManager.unregisterListener(this);
                //if (FileHelper.saveToFile(lastX)){
                  //  Toast.makeText(GroovyDancero.this,"Saved to file",Toast.LENGTH_SHORT).show();
                //}else{
                 //   Toast.makeText(GroovyDancero.this,"Error save file!!!",Toast.LENGTH_SHORT).show();
                //}
                /*
                 * if(writer != null) { try { writer.close(); } catch
                 * (IOException e) { // TODO Auto-generated catch block
                 * e.printStackTrace(); } }
                 */} catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(GroovyDancero.this,"Error save file!!!",Toast.LENGTH_SHORT).show();
            }
            break;
        default:
            break;
    }

}                                                                                                   //SAFE4


                                                                                                    // S A F E
    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText(Float.toString(deltaX));
        currentY.setText(Float.toString(deltaY));
        currentZ.setText(Float.toString(deltaZ));
    }

    // display the max x,y,z accelerometer values
    public void displayMaxValues() {
        if (deltaX > deltaXMax) {
            deltaXMax = deltaX;
            maxX.setText(Float.toString(deltaXMax));
        }
        if (deltaY > deltaYMax) {
            deltaYMax = deltaY;
            maxY.setText(Float.toString(deltaYMax));
        }
        if (deltaZ > deltaZMax) {
            deltaZMax = deltaZ;
            maxZ.setText(Float.toString(deltaZMax));
        }
    }

        // display the max coord values // these also get written to file as the spacial coordin?
    public void displayLastValues()
        {
            coordX.setText(Float.toString(lastX));

            coordY.setText(Float.toString(lastY));

            coordZ.setText(Float.toString(lastZ));
        }
    public void initializeViews() {
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);

        maxX = (TextView) findViewById(R.id.maxX);
        maxY = (TextView) findViewById(R.id.maxY);
        maxZ = (TextView) findViewById(R.id.maxZ);

        coordX = (TextView) findViewById(R.id.x_axis); //added
        coordY = (TextView) findViewById(R.id.y_axis); //added
        coordZ = (TextView) findViewById(R.id.z_axis); //added
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}


//EXTRA GARBO

//   btnSave.setOnClickListener(new View.OnClickListener() {
//     @Override
//   public void onClick(View v) {
//     if(FileHelper.saveToFile(R.id.currentX)) {//, coordX, lastY, coordY, lastZ, coordZ){
//       Toast.makeText(AndroidAccelerometerExample.this,"Saved to file",Toast.LENGTH_SHORT).show();
// }
//else{
//  Toast.makeText(AndroidAccelerometerExample.this,"Error save file!!!",Toast.LENGTH_SHORT).show();
//}
//}
//});
//}

//public void onStopClick(View view) {//  sensorManager.unregisterListener(this); }