package rnstudio.socreg.utils;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import rnstudio.socreg.DB.AppDatabase;
import rnstudio.socreg.SMSServiceView;
import rnstudio.socreg.repository.DataStorePreferenceRepository;

import com.androidnetworking.AndroidNetworking;

public class App extends Application {
    private static AppOpenManager appOpenManager;
    public static App instance;
    public static OkHttpClient.Builder mClientBuilder;
    public static OkHttpClient mClient;
    private static Context context;
    private static DataStorePreferenceRepository dataStorePreferenceRepository;

    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        MobileAds.initialize(
                this,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {}
                });

        appOpenManager = new AppOpenManager(this);

        instance = this;
        RoomDatabase.Callback rdc = new RoomDatabase.Callback() {
            public void onCreate (SupportSQLiteDatabase db) {
                ArrayList<SMSServiceView> list = new ArrayList();
                list.add(new SMSServiceView("vak-sms.com", "", 0));
                list.add(new SMSServiceView("sms-service-online.com", "", 1));
                list.add(new SMSServiceView("onlinesim.ru", "", 2));
                list.add(new SMSServiceView("sms-activate.org", "", 3));

                db.beginTransaction();
                for (int i = 0; i < list.size(); i++) {
                    ContentValues cv = new ContentValues();
                    cv.put("name", list.get(i).getName());
                    cv.put("api_key", list.get(i).getApiKey());
                    cv.put("is_default", list.get(i).isDefault());
                    cv.put("service_id", list.get(i).getServiceId());
                    db.insert("sms_service", 0, cv);
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            }
            public void onOpen (SupportSQLiteDatabase db) {

            }
        };

        Migration migration_1_2 = new Migration(1, 2) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
                database.execSQL("CREATE TABLE `proxy` (`id` INTEGER primary key autoincrement, "
                        + "`host` TEXT, `port` TEXT, `user_name` TEXT, `password` TEXT)");
            }
        };

        database = Room.databaseBuilder(this, AppDatabase.class, "database")
                .addMigrations(migration_1_2)
                .addCallback(rdc)
                .build();

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);

        mClientBuilder = clientBuilder;

        OkHttpClient client = clientBuilder
                .build();
        mClient = client;

        AndroidNetworking.initialize(getApplicationContext(), client);
    }

    public static App getInstance() {
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }

    public static void initializeNetwork(OkHttpClient client){
        AndroidNetworking.initialize(context, client);
    }

    public static DataStorePreferenceRepository getDataStore(){
        if(dataStorePreferenceRepository == null){
            dataStorePreferenceRepository = new DataStorePreferenceRepository(context);
        }
        return dataStorePreferenceRepository;
    }
}
