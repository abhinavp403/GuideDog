package com.example.abhinav.hackholyokeapp;

import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import clarifai2.dto.prediction.Concept;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.speech.tts.TextToSpeech.OnInitListener;

public class RecognizeConceptsAdapter extends RecyclerView.Adapter<RecognizeConceptsAdapter.Holder> implements OnInitListener {
    @NonNull private List<Concept> concepts = new ArrayList<>();
    private TextToSpeech myTTS;

    public RecognizeConceptsAdapter setData(@NonNull List<Concept> concepts) {
        this.concepts = concepts;
        notifyDataSetChanged();
        return this;
    }

    @Override public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_concept, parent, false));
    }

    @Override public void onBindViewHolder(Holder holder, int position) {
        final Concept concept = concepts.get(1);
            holder.label.setText(!holder.label.getText().equals("no person") || !holder.label.getText().equals("indoors") || !holder.label.getText().equals("technology") || !holder.label.getText().equals("business") || !holder.label.getText().equals("electricity") || !holder.label.getText().equals("danger") || !holder.label.getText().equals("one") || !holder.label.getText().equals("abstract") || !holder.label.getText().equals("room") || !holder.label.getText().equals("adult") ?  concept.name(): concept.id());

       // String toSpeak = holder.label.getText().toString();
       // myTTS.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
        /*if (!concept.name().equals("no person") || !concept.name().equals("indoor") || !concept.name().equals("technology") || !concept.name().equals("business") || !concept.name().equals("electricity") || !concept.name().equals("danger") || !concept.name().equals("one") || !concept.name().equals("abstract"))
        {
            return;
        }*/
    }

    @Override public int getItemCount() {
        return concepts.size();
    }

    @Override
    public void onInit(int initStatus) {
        if (initStatus == TextToSpeech.SUCCESS) {
            if (myTTS.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(Locale.US);
        }
    }

    static final class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.label) TextView label;
        @BindView(R.id.probability) TextView probability;

        public Holder(View root) {
            super(root);
            ButterKnife.bind(this, root);
        }
    }
}