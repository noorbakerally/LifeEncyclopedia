package com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.adapters;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.others.LifeObject;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.R;

public class DirectSubclassArrayAdapter extends ArrayAdapter<LifeObject>{
    private final Context context;
    private final List<LifeObject> lifeObjects;
    private int resource;

    public DirectSubclassArrayAdapter(Context context, int resource,List<LifeObject> lifeObjects) {
        super(context, resource, lifeObjects);
        this.context = context;
        this.lifeObjects = lifeObjects;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View rowView = inflater.inflate(resource, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.directSubclassValue);

		/*
		ImageRequest ir = new ImageRequest(lifeObjects.get(position).getThumbnail(), new Response.Listener<Bitmap>() {

		    @Override
		    public void onResponse(Bitmap response) {
		    	ImageView thumbnailImage = (ImageView)rowView.findViewById(R.id.directSubclassThumbnail);
		    	thumbnailImage.setImageBitmap(response);
		    }
		}, 0, 0, null, null);

		EventBus.getDefault().post(new NewRequestEvent(ir));
		*/


        textView.setText(lifeObjects.get(position).getLabel());
        return rowView;
    }
}
