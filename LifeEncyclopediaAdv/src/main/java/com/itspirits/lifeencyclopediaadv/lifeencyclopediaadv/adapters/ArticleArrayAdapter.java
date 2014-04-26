package com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.adapters;

import java.util.List;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.others.Constants;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.others.LifeObject;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.R;
public class ArticleArrayAdapter extends ArrayAdapter<LifeObject>{
	private final Context context;
	private final List<LifeObject> lifeObjects;
	private int resource;
	
	public ArticleArrayAdapter(Context context, int resource,List<LifeObject> lifeObjects) {
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
		TextView textView = (TextView) rowView.findViewById(R.id.loEntity);
		
		final ImageView thumbnailImage = (ImageView)rowView.findViewById(R.id.imgThumbnail);
		/*
		thumbnailImage.getLayoutParams().height = 100;
		thumbnailImage.getLayoutParams().width = 149;
		*/

        if (lifeObjects.get(position).getEntity().equals("Life")){
			thumbnailImage.setImageResource(R.drawable.life149);
			thumbnailImage.getLayoutParams().height = 84;
			thumbnailImage.getLayoutParams().width = 149;
		 }
		 else{
			 ImageRequest ir = new ImageRequest(lifeObjects.get(position).getThumbnail(), new Response.Listener<Bitmap>() {

				    @Override
				    public void onResponse(Bitmap response) {
				    	
				    	thumbnailImage.setImageBitmap(response);
				    }
				}, 0, 0, null, null);
			 Constants.queue.add(ir);
		 }
		
		
		//EventBus.getDefault().post(new NewRequestEvent(ir));
		
		
		textView.setText(lifeObjects.get(position).getLabel());
		
		
		/***changing color of the selected lifeobject**/
		//by default the color of any lifeobject is transparent
		//if currentlifeobject is null, the selected object is the object at location 0
		//else we compare and look for the current lifeobject each time articlearrayadapater is called for each entry in the list
		rowView.setBackgroundColor(Color.TRANSPARENT);
		if (Constants.currentLifeObject != null){
			//System.out.println("ArticleArrayAdapter:"+Constants.currentLifeObject.getLabel()+" "+lifeObjects.get(position).getLabel()+" ");
			if (lifeObjects.get(position).getLabel().equals(Constants.currentLifeObject.getLabel())){
				rowView.setBackgroundColor(Color.LTGRAY);
				Constants.currentLifeObjectPosition = position;
			}
		}
		else if (position == lifeObjects.size() - 1){
			rowView.setBackgroundColor(Color.LTGRAY);
			Constants.currentLifeObjectPosition = position;
		}
		return rowView;
	}
}
