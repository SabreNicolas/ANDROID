package com.example.mylife;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import classBDD.Espace;
import classBDD.Indicateur;

public class MyCustomAdapter extends BaseAdapter implements ListAdapter {
    public ArrayList<Espace> espaces = new ArrayList<Espace>();
    public ArrayList<Indicateur> indicateurs = new ArrayList<Indicateur>();
    public Context context;
    String type = null;


    class JSONAsyncTask extends AsyncTask<String, Void, JSONObject> {
        // Params, Progress, Result

        @Override
        protected void onPreExecute() { // S’exécute dans l’UI Thread
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... qs) {
            // pas d'interaction avec l'UI Thread ici
            // On exécute la requete
            //String res = MainActivity.this.gs.requete(qs[0]);
            String res = null;
            try {
                res = MyCustomAdapter.this.gs.requete(qs[0],"DELETE",null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                JSONObject json = new JSONObject(res);
                return json;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONObject json) { // S’exécute dans l’UI Thread
            if (json != null) {
                System.out.println("Supprimé avec succès !");
            }
        }
    }
    GlobalState gs;



    public MyCustomAdapter(ArrayList<Espace> list, Context context, GlobalState gs, String type) {
        this.type = type;
        this.espaces = list;
        this.context = context;
        this.gs = gs;
    }

    public MyCustomAdapter(ArrayList<Indicateur> list, Context context, GlobalState gs) {
        this.type = "Indicateur";
        this.indicateurs = list;
        this.context = context;
        this.gs = gs;
    }



    @Override
    public int getCount() {
        if(this.type.equals("Espace")){
            return espaces.size();
        }
        else return indicateurs.size();
    }

    @Override
    public Object getItem(int pos) {
        if(this.type.equals("Espace")){
            return espaces.get(pos);
        }
        else return indicateurs.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        if(this.type.equals("Espace")){
            return espaces.get(pos).getId();
        }
        else return indicateurs.get(pos).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_item, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.textBtnCol1);
        if(this.type.equals("Espace")){
            listItemText.setText(espaces.get(position).getNomEspace());
        }
        else listItemText.setText(indicateurs.get(position).getNomIndicateur());


        //Handle buttons and add onClickListeners
        ImageButton deleteBtn = (ImageButton)view.findViewById(R.id.delete_btn);
        ImageButton editBtn = (ImageButton)view.findViewById(R.id.edit_btn);
        ImageButton completeBtn = (ImageButton)view.findViewById(R.id.complete_btn);

        if(this.type.equals("Indicateur")){
            completeBtn.setVisibility(View.INVISIBLE);
        }

        //DELETE
        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                int id = (int) getItemId(position);
                JSONAsyncTask jsAT = new JSONAsyncTask();

                if(type.equals("Espace")){
                    System.out.println("je vais supprimer espace avec id : "+id);
                    jsAT.execute("/espaces/"+id);
                    espaces.remove(position);
                }
                else{
                    System.out.println("je vais supprimer indicateur avec id : "+id);
                    jsAT.execute("/indicateurs/"+id);
                    indicateurs.remove(position);
                }
                notifyDataSetChanged();
            }
        });

        //EDIT
        editBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                int id = (int) getItemId(position);

                if(type.equals("Espace")){
                    System.out.println("je vais editer espace avec id : "+id);
                    gs.setEspace((Espace) getItem(position));
                    Intent versEspace= new Intent(context, AddEspace.class);
                    context.startActivity(versEspace);
                }
                else{
                    System.out.println("je vais editer indicateur avec id : "+id);
                    gs.setIndicateur((Indicateur) getItem(position));
                    Intent versIndicateur= new Intent(context, AddIndicateur.class);
                    context.startActivity(versIndicateur);
                }
                notifyDataSetChanged();
            }
        });

        //REMPLISSAGE
        completeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                int id = (int) getItemId(position);
                System.out.println("je vais remplir des data pour espace avec id : "+id);
                gs.setEspace((Espace) getItem(position));
                Intent versAddData= new Intent(context, AddDataEspace.class);
                context.startActivity(versAddData);
                notifyDataSetChanged();
            }
        });

        return view;
    }
}
