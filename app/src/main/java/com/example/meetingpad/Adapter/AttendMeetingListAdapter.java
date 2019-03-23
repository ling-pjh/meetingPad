package com.example.meetingpad.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.meetingpad.R;
import com.example.meetingpad.entity.PersonLight;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AttendMeetingListAdapter extends RecyclerView.Adapter {

    private int count=0;
    private List<PersonLight> persons;
    private Context context;
    public AttendMeetingListAdapter(Context context, List<PersonLight> persons){

        this.persons=persons;
        this.context=context;
    }

    class HeadImageViewHolder extends RecyclerView.ViewHolder{

        private ImageView headImageView;
        private TextView nameTextView;

        public HeadImageViewHolder(final View itemView) {
            super(itemView);
            nameTextView=itemView.findViewById(R.id.name_in_head_image);
            headImageView=itemView.findViewById(R.id.image_in_head_image);
        }

        public ImageView getHeadImageView() {
            return headImageView;
        }

        public TextView getNameTextView() {
            return nameTextView;
        }


    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HeadImageViewHolder hivh;
            hivh=new HeadImageViewHolder(((Activity)context).getLayoutInflater().inflate(R.layout.head_image,null));
        return hivh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HeadImageViewHolder vh = ((HeadImageViewHolder) holder);
            String name;
            byte[] headImage;
            Bitmap bitmap = null;
            name = persons.get(position).getpName();
            headImage = persons.get(position)
                    .getpIcon();
            if (headImage != null)
                bitmap = BitmapFactory.decodeByteArray(headImage, 0, headImage.length);
            if (name == null) name = "空";
            if (bitmap == null) {
            headImage = getByte(context, R.drawable.ic_account_circle_black_30dp);
            bitmap=BitmapFactory.decodeByteArray(headImage,0,headImage.length);
            }
            vh.getNameTextView().setText(name);
            vh.getHeadImageView().setImageBitmap(bitmap);
        }

    @Override
    public int getItemCount() {
        int size=0;
        if(persons!=null)size=persons.size();
        return size;
    }

    public void setPersons(List<PersonLight> persons) {
        this.persons = persons;
    }

    public  byte[] getByte(Context context, int vectorDrawableId) {
        Bitmap bitmap = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Drawable vectorDrawable = context.getDrawable(vectorDrawableId);
            bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), vectorDrawableId);
        }
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] datas=baos.toByteArray();
        return datas;
    }

}
