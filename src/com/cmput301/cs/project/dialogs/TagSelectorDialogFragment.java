package com.cmput301.cs.project.dialogs;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.adapters.TagsCheckboxesAdapter;
import com.cmput301.cs.project.listeners.TagSelectorListener;
import com.cmput301.cs.project.models.Tag;

import java.util.ArrayList;

/*
 * Will allow the user to select the tags that they want to filter by
 * WARNING: The activity running this class MUST implement TagSelectorListener
 *
 * In the tagsList, every CheckBox getTag will correspond with the Tag it is displaying
 */
public class TagSelectorDialogFragment extends DialogFragment implements CompoundButton.OnCheckedChangeListener {
    private static final String ALL_TAGS = "all_tags";
    private static final String SELECTED_TAGS = "selected_tags";
    private ArrayList<Tag> mWantedTags;

    private TagSelectorListener mListener;

    public static TagSelectorDialogFragment newInstance(ArrayList<Tag> allTags, ArrayList<Tag> currentlyWantedTags) {
        TagSelectorDialogFragment f = new TagSelectorDialogFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(ALL_TAGS, allTags);
        args.putParcelableArrayList(SELECTED_TAGS, currentlyWantedTags);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Standard way to be able to communicate with the Activity
        // Prefer this to trying to cast when button is hit because this throws an error earlier
        try {
            mListener = (TagSelectorListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("The activity " + activity.toString() + " must implement TagSelectorListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Will load all tags inOnCreateView
        mWantedTags = getArguments().getParcelableArrayList(SELECTED_TAGS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tag_selector_dialog, container, false);

        // Set up the ListView
        ListView tagsList = (ListView) view.findViewById(R.id.tagsCheckboxList);
        ArrayList<Tag> allTags = getArguments().getParcelableArrayList(ALL_TAGS);

        TagsCheckboxesAdapter adapter = new TagsCheckboxesAdapter(getActivity(), this, allTags, mWantedTags);

        tagsList.setAdapter(adapter);

        // Set up the button
        Button button = (Button) view.findViewById(R.id.filterButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mListener.wantedTagsChanged(mWantedTags);
                dismiss();
            }
        });

        getDialog().setTitle(getActivity().getString(R.string.filter_by_tag));

        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean b) {
        Tag correspondingTag = (Tag) button.getTag();

        if (button.isChecked()) {
            mWantedTags.add(correspondingTag);
        } else {
            mWantedTags.remove(correspondingTag);
        }
    }
}