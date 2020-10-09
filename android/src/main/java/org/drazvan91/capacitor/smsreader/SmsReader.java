package org.drazvan91.capacitor.smsreader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import org.json.JSONArray;
import org.json.JSONObject;

@NativePlugin(requestCodes = { SmsReader.REQUEST_CODE })
public class SmsReader extends Plugin {
    protected static final int REQUEST_CODE = 12345;

    @SuppressLint("NewApi")
    @PluginMethod
    public void read(PluginCall call) {
        saveCall(call);
        this.pluginRequestPermission(Manifest.permission.READ_SMS, REQUEST_CODE);
    }

    @Override
    protected void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PluginCall savedCall = getSavedCall();
        if (savedCall == null) {
            Log.d("SmsReader", "No stored plugin call for permission request result");
            return;
        }

        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                Log.d("SmsReader", "User denied permission");
                return;
            }
        }

        if (requestCode == REQUEST_CODE) {
            loadSmsList(savedCall);
        }
    }

    private void loadSmsList(PluginCall savedCall) {
        int skip = savedCall.getInt("skip", 0);
        int take = savedCall.getInt("take", 10);

        JSONArray items = new JSONArray();
        Cursor cursor = this.getContext().getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

        int index = -1;
        while (cursor.moveToNext()) { // must check the result to prevent exception
            index++;
            if (index < skip) {
                continue;
            }
            if (index >= skip + take) {
                break;
            }

            JSONObject json = this.getJsonFromCursor(cursor);
            items.put(json);
        }
        cursor.close();

        JSObject ret = new JSObject();
        ret.put("items", items);
        savedCall.success(ret);
    }

    //    private PluginResult listSMS(JSONObject filter, CallbackContext callbackContext) {
    //        Log.i(LOGTAG, ACTION_LIST_SMS);
    //        String uri_filter = filter.has(BOX) ? filter.optString(BOX) : "inbox";
    //        int fread = filter.has(READ) ? filter.optInt(READ) : -1;
    //        int fid = filter.has("_id") ? filter.optInt("_id") : -1;
    //        String faddress = filter.optString(ADDRESS);
    //        String fcontent = filter.optString(BODY);
    //        int indexFrom = filter.has("indexFrom") ? filter.optInt("indexFrom") : 0;
    //        int maxCount = filter.has("maxCount") ? filter.optInt("maxCount") : 10;
    //        JSONArray jsons = new JSONArray();
    //        Activity ctx = this.cordova.getActivity();
    //        Uri uri = Uri.parse((SMS_URI_ALL + uri_filter));
    //        Cursor cur = ctx.getContentResolver().query(uri, (String[]) null, "", (String[]) null, null);
    //        int i = -1;
    //        while (cur.moveToNext()) {
    //            JSONObject json;
    //            boolean matchFilter = false;
    //            if (fid > -1) {
    //                matchFilter = (fid == cur.getInt(cur.getColumnIndex("_id")));
    //            } else if (fread > -1) {
    //                matchFilter = (fread == cur.getInt(cur.getColumnIndex(READ)));
    //            } else if (faddress.length() > 0) {
    //                matchFilter = faddress.equals(cur.getString(cur.getColumnIndex(ADDRESS)).trim());
    //            } else if (fcontent.length() > 0) {
    //                matchFilter = fcontent.equals(cur.getString(cur.getColumnIndex(BODY)).trim());
    //            } else {
    //                matchFilter = true;
    //            }
    //            if (!matchFilter) continue;
    //
    //            ++i;
    //            if (i < indexFrom) continue;
    //            if (i >= indexFrom + maxCount) break;
    //
    //            if ((json = this.getJsonFromCursor(cur)) == null) {
    //                callbackContext.error("failed to get json from cursor");
    //                cur.close();
    //                return null;
    //            }
    //            jsons.put((Object) json);
    //        }
    //        cur.close();
    //        callbackContext.success(jsons);
    //        return null;
    //    }

    private JSONObject getJsonFromCursor(Cursor cur) {
        JSONObject json = new JSONObject();

        int nCol = cur.getColumnCount();
        String keys[] = cur.getColumnNames();

        try {
            for (int j = 0; j < nCol; j++) {
                String keyName = getKeyName(keys[j]);

                switch (cur.getType(j)) {
                    case Cursor.FIELD_TYPE_NULL:
                        json.put(keyName, null);
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        json.put(keyName, cur.getLong(j));
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        json.put(keyName, cur.getFloat(j));
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        json.put(keyName, cur.getString(j));
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        json.put(keyName, cur.getBlob(j));
                        break;
                }
            }
        } catch (Exception e) {
            return null;
        }

        return json;
    }

    private String getKeyName(String rawKeyName) {
        switch (rawKeyName) {
            case "_id":
                return "id";
            case "thread_id":
                return "threadId";
            case "date_sent":
                return "dateSent";
            case "reply_path_present":
                return "replyPathPresent";
            case "sub_id":
                return "subId";
            case "error_code":
                return "errorCode";
            default:
                return rawKeyName;
        }
    }
}
