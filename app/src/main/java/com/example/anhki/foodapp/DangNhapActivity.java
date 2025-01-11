package com.example.anhki.foodapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.anhki.foodapp.DAO.NhanVienDAO;
import com.example.food_app.R;

public class DangNhapActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edTenDangNhap, edMatKhau;
    private Button btnDongY;
    private NhanVienDAO nhanVienDAO;

    private final int STORAGE_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dangnhap);

        edTenDangNhap = findViewById(R.id.edTenDangNhapDN);
        edMatKhau = findViewById(R.id.edMatKhauDN);

        btnDongY = findViewById(R.id.btnDongYDN);

        nhanVienDAO = new NhanVienDAO(this);

        // Kiểm tra quyền truy cập bộ nhớ
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission();
        }

        // Set sự kiện cho nút "Đồng ý" để thực hiện đăng nhập
        btnDongY.setOnClickListener(this);
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Ứng dụng cần được cấp quyền")
                    .setMessage("Ứng dụng cần được cấp quyền truy cập bộ nhớ để có thể sử dụng ứng dụng tốt hơn!")
                    .setPositiveButton("Ok", (dialog, which) -> ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE))
                    .setNegativeButton("Hủy", (dialog, which) -> System.exit(0))
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Đã được cấp quyền!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Ứng dụng bị từ chối cấp quyền!", Toast.LENGTH_SHORT).show();
                requestStoragePermission();
            }
        }
    }

    private void btnDongY() {
        String sTenDangNhap = edTenDangNhap.getText().toString();
        String sMatKhau = edMatKhau.getText().toString();

        // Kiểm tra thông tin đăng nhập
        int kiemtra = nhanVienDAO.KiemTraDangNhap(sTenDangNhap, sMatKhau);
        int maquyen = nhanVienDAO.LayQuyenNhanVien(kiemtra);

        if (kiemtra != 0) {
            // Lưu quyền đăng nhập vào SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("luuquyen", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("maquyen", maquyen);
            editor.apply();

            // Chuyển sang màn hình TrangChuActivity
            Intent iTrangChu = new Intent(DangNhapActivity.this, TrangChuActicity.class);
            iTrangChu.putExtra("tendn", sTenDangNhap);
            iTrangChu.putExtra("manhanvien", kiemtra);
            startActivity(iTrangChu);

            // Đóng activity DangNhapActivity
            finish();

            // Hiệu ứng chuyển trang
            overridePendingTransition(R.anim.hieuung_activity_vao, R.anim.hieuung_activity_ra);
        } else {
            Toast.makeText(DangNhapActivity.this, "Đăng nhập thất bại!!", Toast.LENGTH_SHORT).show();
        }
    }
    private void BtnDangKy() {
        Intent iTrangChu = new Intent(DangNhapActivity.this, DangKyActivity.class);
        startActivity(iTrangChu);
        finish();
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnDongYDN) {
            btnDongY();
        }
        if (id == R.id.btnDangKyDN) {
            BtnDangKy();
        }
    }
}
