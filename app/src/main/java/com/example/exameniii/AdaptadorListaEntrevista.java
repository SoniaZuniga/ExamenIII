package com.example.exameniii;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class AdaptadorListaEntrevista extends RecyclerView.Adapter<AdaptadorListaEntrevista.ViewHolder> {
    private List<Entrevista> info;
    private LayoutInflater inflater;
    private Context context;
    private AdaptadorListaEntrevista.OnItemDoubleClickListener doubleClickListener;
    public static int itemSelect= -1;
    ImageView imagen;

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    public interface OnItemDoubleClickListener {
        void onItemDoubleClick(Entrevista entrevista);

    }
    public AdaptadorListaEntrevista(List<Entrevista> itemList, Context context, AdaptadorListaEntrevista.OnItemDoubleClickListener doubleClickListener){
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.info = itemList;
        this.doubleClickListener = doubleClickListener;

    }
    @Override
    public int getItemCount() {
        return info.size();
    }

    public static int getItemSelecionado(){
        return itemSelect;
    }

    @Override
    public AdaptadorListaEntrevista.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.cardview, null);
        return new AdaptadorListaEntrevista.ViewHolder(view);
    }
    public void setItems(List<Entrevista> items){
        info = items;
    }
    @Override
    public void onBindViewHolder(final AdaptadorListaEntrevista.ViewHolder holder, final int position) {
        holder.bindData(info.get(position));

        final int currentPosition = position;
        holder.itemView.setSelected(position == itemSelect);

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                itemSelect = currentPosition;
            }
        });

    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_descripcion, txt_periodista, txt_fecha, txt_id;

        ViewHolder(View itemView) {
            super(itemView);
            txt_descripcion = (TextView) itemView.findViewById(R.id.txt_descripcion );
            txt_periodista = (TextView) itemView.findViewById(R.id.txt_periodista);
            txt_fecha = (TextView) itemView.findViewById(R.id.txt_fecha);
            txt_id = (TextView) itemView.findViewById(R.id.txt_id);
            imagen = (ImageView) itemView.findViewById(R.id.imagen);
        }

        void bindData(final Entrevista entrevista){
            txt_descripcion.setText(entrevista.getDescripcion());
            txt_periodista.setText(entrevista.getPeriodista());
            txt_fecha.setText(entrevista.getFecha());
            txt_id.setText(entrevista.getId());
            //Bitmap img = foto_decode(entrevista.getImagen());
            //imagen.setImageBitmap(img);
        }
    }

}
