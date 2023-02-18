package com.example.abcs;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Payment_hostel_fees_invoice extends AppCompatActivity {
    Display mDisplay;
    String imagesUri;
    String path;
    Bitmap bitmap;

    int totalHeight;
    int totalWidth;

    //firbase declaration part
    FirebaseAuth auth;
    String invo_userid;
    FirebaseFirestore fstore;
    TextView remamount;


    public static final int READ_PHONE = 110;
    String file_name = "collage payment section";
    File myPath;
    TextView tv_invoiceno;
    TextView invoice_send_amount;
    Button btn;
    TextView section;
    ImageView sign;


    TextView invoi_student_name,invoice_student_class,invoice_student_rollno,invoice_student_dept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_hostel_fees_invoice);
        tv_invoiceno=findViewById(R.id.tv_invoiceno3);
        invoice_send_amount=findViewById(R.id.tv_studentalpayamo3);
        btn=findViewById(R.id.btn_print3);
        sign=findViewById(R.id.idIVQrcode3);
        section=findViewById(R.id.tv_section3);
        invoi_student_name=findViewById(R.id.tv_StudentName3);
        invoice_student_class=findViewById(R.id.tv_Studentclass3);
        invoice_student_rollno=findViewById(R.id.tv_StudentID3);

        invoice_send_amount=findViewById(R.id.tv_studentalpayamo3);

        invoice_student_dept=findViewById(R.id.tv_invoidept113);
         // invoice_student_paidamount=findViewById(R.id.tv_amountpaid)

        auth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        invo_userid=auth.getCurrentUser().getUid();

        sign.setVisibility(View.INVISIBLE);
        section.setVisibility(View.INVISIBLE);

//text set for invoice
     String    txt_remamount=getIntent().getStringExtra("amo3");
        invoice_send_amount.setText(txt_remamount);

        DocumentReference reference = fstore.collection("demo").document(invo_userid);
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //basic profile things


                String pro_name= documentSnapshot.getString("Name");
                String pro_rollno=documentSnapshot.getString("Rollno");
                String pro_dept=documentSnapshot.getString("Branch");
                String pro_class=documentSnapshot.getString("_Class");
                //setdata
                invoi_student_name.setText(pro_name);
                invoice_student_rollno.setText(pro_rollno);
                invoice_student_dept.setText(pro_dept);
                invoice_student_class.setText(pro_class);

            }
        });



        //pdf size

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mDisplay = wm.getDefaultDisplay();
        if(Build.VERSION.SDK_INT >= 23){
            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED){
            }else{
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PHONE);
            }
        }
        //click event

        invoice_no();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //callling method

                //visibility changes
                Print_Button_invisible();
                uplod_paymentdata1st(invo_userid, invoi_student_name, invoice_student_class, invoice_student_rollno, invoice_student_dept, tv_invoiceno, invoice_send_amount);
                //takking screenshot and making pdf of its
                takeScreenShot();
            }
        });


    }

    private void uplod_paymentdata1st(String invo_userid, TextView invoi_student_name, TextView invoice_student_class, TextView invoice_student_rollno, TextView invoice_student_dept, TextView tv_invoiceno, TextView invoice_send_amount) {
        String txt_na = invoi_student_name.getText().toString();
        String txt_class = invoice_student_class.getText().toString();
        String txt_roll = invoice_student_rollno.getText().toString();
        String txt_dept = invoice_student_dept.getText().toString();
        String txt_invoic = tv_invoiceno.getText().toString();
        String txt_invamount = invoice_send_amount.getText().toString();


        int to = 11000;
        int pay = Integer.parseInt(txt_invamount);
       // int curr=Integer.parseInt(pass);
        int rem = to -pay;
        String txt_rem = Integer.toString(rem);




        DocumentReference reference = fstore.collection("Hostel_fees_data").document(invo_userid);
        Map<String, String> v = new HashMap<>();
        v.put("_1Student_roll_no", txt_roll);
        v.put("_1Student_name", txt_na);
        v.put("_1Student_class", txt_class);
        v.put("_1Student_dept", txt_dept);
        v.put("_1Student_invoiceno", txt_invoic);
        //v.put("1Student_total_fees", pass);
        v.put("_1Student_payed_amount", txt_invamount);
        v.put("_1Student_remain_fees", txt_rem);

        reference.set(v).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(Payment_hostel_fees_invoice.this, "your payment record save", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void takeScreenShot() {
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ScreenShot/");

        if(!folder.exists()){
            boolean success = folder.mkdir();
        }

        path = folder.getAbsolutePath();
        path = path + "/" + file_name + System.currentTimeMillis() + ".pdf";


        View u = findViewById(R.id.ticket3);

        NestedScrollView z = findViewById(R.id.ticket3);
        totalHeight = z.getChildAt(0).getHeight();
        totalWidth = z.getChildAt(0).getWidth();




        String extr = Environment.getExternalStorageDirectory() + "/Flight Ticket/";
        File file = new File(extr);
        if(!file.exists())
            file.mkdir();
        String fileName = file_name + ".jpg";
        myPath = new File(extr, fileName);
        imagesUri = myPath.getPath();
        bitmap = getBitmapFromView(u, totalHeight, totalWidth);

        try{
            FileOutputStream fos = new FileOutputStream(myPath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        createPdf();
    }

    private void createPdf() {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        canvas.drawPaint(paint);

        Bitmap bitmap = Bitmap.createScaledBitmap(this.bitmap, this.bitmap.getWidth(), this.bitmap.getHeight(), true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);
        File filePath = new File(path);
        try{
            document.writeTo(new FileOutputStream(filePath));
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(this, "Something Wrong: "+e.toString(), Toast.LENGTH_SHORT).show();
        }

        document.close();

        if (myPath.exists())
            myPath.delete();

        openPdf(path);

    }

    private void openPdf(String path) {
        File file = new File(path);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(file), "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent intent = Intent.createChooser(target, "Open FIle");
        try{



            startActivity(intent);
        }catch (ActivityNotFoundException e){
            Toast.makeText(this, "No Apps to read PDF FIle", Toast.LENGTH_SHORT).show();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                Intent intent1=new Intent(Payment_hostel_fees_invoice.this,Payment_home_page.class);
                startActivity(intent1);

            }
        }, 5000);



    }

    private Bitmap getBitmapFromView(View view, int totalHeight, int totalWidth) {
        Bitmap returnedBitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();

        if(bgDrawable != null){
            bgDrawable.draw(canvas);
        }else{
            canvas.drawColor(Color.WHITE);
        }

        view.draw(canvas);
        return returnedBitmap;

    }

    private void Print_Button_invisible() {
        btn.setVisibility(View.INVISIBLE);
        sign.setVisibility(View.VISIBLE);
        section.setVisibility(View.VISIBLE);
    }

    private void invoice_no() {
//
//        Random random=new Random();
//        int val=random.nextInt(1000000000);
//        int val2=random.nextInt(1000000000);
//        if(val ==val2){
//            tv_invoiceno.setText("E0"+val);
//        }

        tv_invoiceno.setText(getIntent().getStringExtra("transactionid"));


    }
}