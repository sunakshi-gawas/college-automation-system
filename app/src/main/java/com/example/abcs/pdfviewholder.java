package com.example.abcs;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class pdfviewholder extends RecyclerView.ViewHolder {

    TextView mName;
    TextView mLink;
    Button mDownload;

    public pdfviewholder(@NonNull View itemView) {
        super(itemView);

        mName = itemView.findViewById(R.id.installment);
        mLink = itemView.findViewById(R.id.link);
        mDownload = itemView.findViewById(R.id.down);


    }
}