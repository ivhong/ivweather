package com.ivhong.ivweather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by wangchanghong on 2017/12/29.
 */

public abstract class CheckPermission extends AppCompatActivity {
    protected String[] needPermissions = new String[]{};
    protected ArrayList<Permission> permissions;

    protected Boolean _checkpermission(){
        if(needPermissions.length > 0){
            ArrayList<String> permissions = new ArrayList<>();

            for(String permission:needPermissions){
                if (ContextCompat.checkSelfPermission(this, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(permission);
                }
            }

            if(permissions.size() > 0){
                ActivityCompat.requestPermissions(this, (String[]) permissions.toArray(),
                        1);//自定义的code

                return false;
            }

        }

        return true;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1 && grantResults.length > 0){
            for(int i:grantResults){
                if(i != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"你必须同意我的权限，所有的！！！", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }

        requestPermissionsCallback();

    }

    protected void add(String name, Boolean required){
        Permission permission = new Permission(name, required);
        permissions.add(permission);
    }

    protected abstract void requestPermissionsCallback();

    class Permission{
        private String name;
        private Boolean required;
        private Boolean status;

        private static final int STATUS_YES = 1;
        private static final int STATUS_NO = 0;

        public Permission(String name, Boolean required) {
            this.name = name;
            this.required = required;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getRequired() {
            return required;
        }

        public void setRequired(Boolean required) {
            this.required = required;
        }

        public Boolean getStatus() {
            return status;
        }

        public void setStatus(Boolean status) {
            this.status = status;
        }

        public void callback(){};
    }
}
