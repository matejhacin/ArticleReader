package com.matejhacin.articlereader;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends Fragment implements View.OnClickListener {

    /*
    Variables
     */

    // Static keys
    private static final String IMAGE_TITLE = "title";
    private static final String IMAGE_DESCRIPTION = "description";
    private static final String IMAGE_FILENAME = "fileName";

    // Views
    private ImageView pictureImageView;
    private RelativeLayout returnButtonLayout;
    private TextView descriptionTextView;

    // Data
    private String title;
    private String description;
    private String fileName;

    /*
    Consutrctor
     */

    public ImageFragment() {
        // Required empty public constructor
    }

    /*
    Lifecycle
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            title = getArguments().getString(IMAGE_TITLE);
            description = getArguments().getString(IMAGE_DESCRIPTION);
            fileName = getArguments().getString(IMAGE_FILENAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_image, container, false);

        // Get views
        pictureImageView = (ImageView) view.findViewById(R.id.pictureImageView);
        descriptionTextView = (TextView) view.findViewById(R.id.descriptionTextView);
        returnButtonLayout = (RelativeLayout) view.findViewById(R.id.returnButtonLayout);

        // Click listener
        returnButtonLayout.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set Image to ImgeView
        File imageFile = new File(Environment.getExternalStorageDirectory() + "/WaldorfPictures/" + fileName);

        if (imageFile.exists()) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            pictureImageView.setImageBitmap(imageBitmap);
        }

        // Set description
        descriptionTextView.setText(Html.fromHtml(description));
    }

    /*
    Callbacks
     */

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.returnButtonLayout:
                getActivity().finish();
                break;
        }
    }

    /*
    Methods
     */

    public static ImageFragment newInstance(String title, String description, String fileName) {
        ImageFragment imageFragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE_TITLE, title);
        args.putString(IMAGE_DESCRIPTION, description);
        args.putString(IMAGE_FILENAME, fileName);
        imageFragment.setArguments(args);
        return imageFragment;
    }
}
