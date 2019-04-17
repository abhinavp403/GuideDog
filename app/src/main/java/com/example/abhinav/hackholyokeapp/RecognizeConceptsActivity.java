package com.example.abhinav.hackholyokeapp;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ViewSwitcher;
import butterknife.BindView;
import butterknife.OnClick;
import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiImage;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.ConceptModel;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RecognizeConceptsActivity extends BaseActivity {
    byte[] imageBytes= new byte[1000];

    // the list of results that were returned from the API
    @BindView(R.id.resultsList) RecyclerView resultsList;

    // the view where the image the user selected is displayed
    //@BindView(R.id.image) ImageView imageView;

    // switches between the text prompting the user to hit the FAB, and the loading spinner
    @BindView(R.id.switcher) ViewSwitcher switcher;

    // the FAB that the user clicks to select an image
    //@BindView(R.id.fab) View fab;

    @NonNull private final RecognizeConceptsAdapter adapter = new RecognizeConceptsAdapter();

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent in= getIntent();
        imageBytes= (byte[]) in.getExtras().get("photo");
        if(imageBytes != null) {
            onImagePicked(imageBytes);
        }
    }

    @Override protected void onStart() {
        super.onStart();
        resultsList.setLayoutManager(new LinearLayoutManager(this));
        resultsList.setAdapter(adapter);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent in= getIntent();
        imageBytes= (byte[]) in.getExtras().get("photo");
        if (imageBytes != null) {
            onImagePicked(imageBytes);
        }
    }

    private void onImagePicked(@NonNull final byte[] imageBytes) {
        // Now we will upload our image to the Clarifai API
        /*t1= new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });*/
        setBusy(true);

        // Make sure we don't show a list of old concepts while the image is being uploaded
        adapter.setData(Collections.<Concept>emptyList());

        new AsyncTask<Void, Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>>() {
            @Override protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(Void... params) {
                // The default Clarifai model that identifies concepts in images

                ClarifaiClient client = new ClarifaiBuilder(getString(R.string.clarifai_api_key))
                        // Optionally customize HTTP client via a custom OkHttp instance
                        .client(new OkHttpClient.Builder()
                                .readTimeout(30, TimeUnit.SECONDS) // Increase timeout for poor mobile networks

                                // Log all incoming and outgoing data
                                // NOTE: You will not want to use the BODY log-level in production, as it will leak your API request details
                                // to the (publicly-viewable) Android log
                                .addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                                    @Override public void log(String logString) {
                                        Timber.e(logString);
                                    }
                                }).setLevel(HttpLoggingInterceptor.Level.BODY))
                                .build()
                        )
                        .buildSync(); // use build() instead to get a Future<ClarifaiClient>, if you don't want to block this thread

                final ConceptModel generalModel =  client.getDefaultModels().generalModel();

                // Use this model to predict, with the image that the user just selected as the input
                return generalModel.predict()
                        .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(imageBytes)))
                        .executeSync();
            }

            @Override protected void onPostExecute(ClarifaiResponse<List<ClarifaiOutput<Concept>>> response) {
                setBusy(false);
                if (!response.isSuccessful()) {
                    showErrorSnackbar(R.string.error_while_contacting_api);
                    return;
                }
                final List<ClarifaiOutput<Concept>> predictions = response.get();
                if (predictions.isEmpty()) {
                    showErrorSnackbar(R.string.no_results_from_api);
                    return;
                }
                adapter.setData(predictions.get(0).data());
                //imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));

                /*t1= new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR) {
                            t1.setLanguage(Locale.UK);
                        }
                    }
                });

                String toSpeak = response.toString();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);*/
            }

            private void showErrorSnackbar(@StringRes int errorString) {
                Snackbar.make(root, errorString, Snackbar.LENGTH_INDEFINITE).show();
            }
        }.execute();
    }

    @Override protected int layoutRes() { return R.layout.activity_recognize; }

    private void setBusy(final boolean busy) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                switcher.setDisplayedChild(busy ? 1 : 0);
            }
        });
    }

}