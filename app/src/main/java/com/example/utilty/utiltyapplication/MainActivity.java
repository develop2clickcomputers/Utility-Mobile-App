package com.example.utilty.utiltyapplication;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

    private static final String TODO = "sudhan";
    private static final String TAG = "sudhan";
    IntentFilter iFilter;
    Intent batteryStatus;
    float batteryPct;
    TextView BatteryPercentage, MobileCharging, MobileDataUage, OperatingSystem, VersionCode,
            ProcessorName, ModelName, RAMName, ManufactureName, SerialNumber, SIMType, OperatorName,
            IEMINumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.toolbar));

        BatteryPercentage = (TextView) findViewById(R.id.battery_percentage);
        MobileCharging = (TextView) findViewById(R.id.mobile_charging);
        MobileDataUage = (TextView) findViewById(R.id.mobile_data);
        OperatingSystem = (TextView) findViewById(R.id.Os_name);
        VersionCode = (TextView) findViewById(R.id.os_version);
        ProcessorName = (TextView) findViewById(R.id.processor_name);
        ModelName = (TextView) findViewById(R.id.modelname);
        RAMName = (TextView) findViewById(R.id.ram_name);
        ManufactureName = (TextView) findViewById(R.id.manufacture_name);
        SerialNumber = (TextView) findViewById(R.id.serial_number);
        SIMType = (TextView) findViewById(R.id.sim_type);
        OperatorName = (TextView) findViewById(R.id.operator_name);
        IEMINumber = (TextView) findViewById(R.id.iemi_number);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);

        } else {

            SIMSlotNo();
        }

        AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccounts();

        for (Account ac : accounts) {
            String acname = ac.name;
            String actype = ac.type;
            // Take your time to look at all available accounts
            System.out.println("Accounts : " + acname + ", " + actype);
        }


        Log.i("TAG", "SERIAL: " + Build.SERIAL);
        Log.i("TAG", "MODEL: " + Build.MODEL);
        Log.i("TAG", "ID: " + Build.ID);
        Log.i("TAG", "Manufacture: " + Build.MANUFACTURER);
        Log.i("TAG", "brand: " + Build.BRAND);
        Log.i("TAG", "type: " + Build.TYPE);
        Log.i("TAG", "user: " + Build.USER);
        Log.i("TAG", "BASE: " + Build.VERSION_CODES.BASE);
        Log.i("TAG", "INCREMENTAL " + Build.VERSION.INCREMENTAL);
        Log.i("TAG", "SDK  " + Build.VERSION.SDK);
        Log.i("TAG", "BOARD: " + Build.BOARD);
        Log.i("TAG", "BRAND " + Build.BRAND);
        Log.i("TAG", "HOST " + Build.HOST);
        Log.i("TAG", "FINGERPRINT: " + Build.FINGERPRINT);
        Log.i("TAG", "Version Code: " + Build.VERSION.RELEASE);


        ModelName.setText(Build.MODEL);
        ManufactureName.setText(Build.MANUFACTURER);
        SerialNumber.setText(Build.SERIAL);

        Field[] fields = Build.VERSION_CODES.class.getFields();
        String name = fields[Build.VERSION.SDK_INT + 1].getName();

        if (name.equals("O")) name = "Oreo";
        if (name.equals("N")) name = "Nougat";
        if (name.equals("M")) name = "Marshmallow";

        if (name.startsWith("O_")) name = "Oreo++";
        if (name.startsWith("N_")) name = "Nougat++";

        OperatingSystem.setText(name);
        VersionCode.setText(Build.VERSION.RELEASE);

        System.out.println("OS Name " + name);

//        String cpuname = getCpuName();
//
//        System.out.println("CPU Name" + cpuname);
//
//        String TotalMemory = getTotalRAM();
//
//        System.out.println("Total internal memory" + TotalMemory);
//
//
//        System.out.println("IMEI Number" + getDeviceImei());
//
//
//        System.out.println("SIM Slot"+SIMImeiDualSlot());

//        String CPUInfo = readCPUinfo();
//
//        System.out.println("CPU Info "+ CPUInfo);

        MobileDataUsage();
        Battery();

        ProcessorName.setText(getCpuName());
        RAMName.setText(getTotalRAM());

        System.out.println("Mobile number" +getMy10DigitPhoneNumber());
        System.out.println("Read the hotstop" +readArpCache());

    }


    //........... Mobile Data Usage ...................

    public String MobileDataUsage() {

        int Datausage = (int) android.net.TrafficStats.getMobileRxBytes();

        String mobileDataUage = getFileSize(Datausage);

        MobileDataUage.setText(mobileDataUage);

        return null;
    }


    //........... Converting Bytes Data ................

    public static String getFileSize(long size) {
        if (size <= 0)
            return "0";

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


    //............. Getting CPU or Processor Name ..............

    @Nullable
    public static String getCpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            br.close();
            String[] array = text.split(":\\s+", 2);
            if (array.length >= 2) {
                return array[1];
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //............. Getting Total Memory ..........

    public String getTotalRAM() {

        RandomAccessFile reader = null;
        String load = null;
        DecimalFormat twoDecimalForm = new DecimalFormat("#.##");
        double totRam = 0;
        String lastValue = "";
        try {
            reader = new RandomAccessFile("/proc/meminfo", "r");
            load = reader.readLine();

            // Get the Number value from the string
            Pattern p = Pattern.compile("(\\d+)");
            Matcher m = p.matcher(load);
            String value = "";
            while (m.find()) {
                value = m.group(1);
                // System.out.println("Ram : " + value);
            }
            reader.close();

            totRam = Double.parseDouble(value);
            // totRam = totRam / 1024;

            double mb = totRam / 1024.0;
            double gb = totRam / 1048576.0;
            double tb = totRam / 1073741824.0;

            if (tb > 1) {
                lastValue = twoDecimalForm.format(tb).concat(" TB");
            } else if (gb > 1) {
                lastValue = twoDecimalForm.format(gb).concat(" GB");
            } else if (mb > 1) {
                lastValue = twoDecimalForm.format(mb).concat(" MB");
            } else {
                lastValue = twoDecimalForm.format(totRam).concat(" KB");
            }


        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            // Streams.close(reader);
        }

        return lastValue;
    }


    public static String readCPUinfo() {

        ProcessBuilder processBuilder;
        String cpuDetails = "";
        String[] DATA = {"/system/bin/cat", "/proc/cpuinfo"};
        InputStream is;
        Process process;
        byte[] bArray;
        bArray = new byte[1024];

        try {
            processBuilder = new ProcessBuilder(DATA);

            process = processBuilder.start();

            is = process.getInputStream();


            while (is.read(bArray) != -1) {

                cpuDetails = cpuDetails + new String(bArray);   //Stroing all the details in cpuDetails
            }
            is.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return cpuDetails;
    }

    //............ Getting IEMI Number ...........

    @SuppressLint({"MissingPermission", "HardwareIds"})
    public String getDeviceImei() {

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imei = "";
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            assert telephonyManager != null;
            imei = telephonyManager.getImei();
        } else {
            assert telephonyManager != null;
            imei = telephonyManager.getDeviceId();
        }
        return imei;
    }

    //............ Getting SIM Slot .................

    public String SIMImeiDualSlot() {

        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class<?> telephonyClass = Class.forName(manager.getClass().getName());
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getFirstMethod = telephonyClass.getMethod("getDeviceId", parameter);
            Log.d("SimData", getFirstMethod.toString());
            Object[] obParameter = new Object[1];
            obParameter[0] = 0;
            String first = (String) getFirstMethod.invoke(manager, obParameter);
            Log.d("IMEI ", "first :" + first);
            obParameter[0] = 1;
            String second = (String) getFirstMethod.invoke(manager, obParameter);
            Log.d("IMEI ", "Second :" + second);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //.............. Finding The Device Dual SIM Slot And  Default device ID ........

    @SuppressLint({"MissingPermission", "HardwareIds", "SetTextI18n"})
    public String SIMSlotNo() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            if (manager.getPhoneCount() == 1) {
                SIMType.setText("Single");
                IEMINumber.setText("Slot 1: " + String.valueOf(manager.getDeviceId(0)));
            } else {
                SIMType.setText("Dual");
                IEMINumber.setText("Slot 1: " + String.valueOf(manager.getDeviceId(0)) + "\n" +
                        "Slot 2: " + String.valueOf(manager.getDeviceId(1)));
            }

            OperatorName.setText(manager.getNetworkOperatorName());

            Log.i("TAG", "Single or Dual Sim " + manager.getPhoneCount());
            Log.i("TAG", "Default device ID " + manager.getDeviceId());
            Log.i("TAG", "Single 1 " + manager.getDeviceId(0));
            Log.i("TAG", "Single 2 " + manager.getDeviceId(1));
            Log.i("TAG", "Operator Name " + manager.getNetworkOperatorName());
            Log.i("TAG", "SimSerialNumber " + manager.getSimSerialNumber());
            Log.i("TAG", "Line1Number " + manager.getLine1Number());
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionManager subscriptionManager = SubscriptionManager.from(getApplicationContext());
            List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();

            Log.d("Test", "Current list = " + subsInfoList);

            for (SubscriptionInfo subscriptionInfo : subsInfoList) {

                String number = subscriptionInfo.getNumber();

                Log.d("Test", " Number is  " + number);
            }
        }


        return null;
    }

    //.............. Battery Usage ...................

    public String Battery() {

        iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = this.registerReceiver(null, iFilter);

        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

        batteryPct = (level / (float) scale) * 100;

        int batteryPercentage = (int) batteryPct;

        System.out.println("Battery percent" + batteryPercentage);

        assert batteryStatus != null;
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        BatteryPercentage.setText(String.valueOf(batteryPercentage) + " %");

        if (isCharging) {
            MobileCharging.setText("Yes");

        } else {
            MobileCharging.setText("No");
        }

        return null;

    }


    private String getMyPhoneNumber() {
        TelephonyManager mTelephonyMgr;
        mTelephonyMgr = (TelephonyManager)
                getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return TODO;
        }
        return mTelephonyMgr.getLine1Number();
    }

    private String getMy10DigitPhoneNumber(){
        String s = getMyPhoneNumber();
        return s != null && s.length() > 2 ? s.substring(2) : null;
    }

    private static ArrayList<String> readArpCache()
    {
        ArrayList<String> ipList = new ArrayList<String>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"), 1024);
            String line;
            while ((line = br.readLine()) != null) {
                Log.d(TAG  ,line);

                String[] tokens = line.split(" +");
                if (tokens != null && tokens.length >= 4) {
                    // verify format of MAC address
                    String macAddress = tokens[3];
                    if (macAddress.matches("..:..:..:..:..:..")) {
                        Log.i(TAG, "MAC=" + macAddress + " IP=" + tokens[0] + " HW=" + tokens[1]);

                        // Ignore the entries with MAC-address "00:00:00:00:00:00"
                        if (!macAddress.equals("00:00:00:00:00:00")) {

                            String ipAddress = tokens[0];
                            ipList.add(ipAddress);

                            Log.i(TAG, macAddress + "; " + ipAddress);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return ipList;
    }
}
