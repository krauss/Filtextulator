package org.krauss.filtextulator;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import org.krauss.filtextulator_new.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jrkrauss on 27/03/17.
 *
 * Class that manages the adding and removing actions within the RecyclerView list.
 *
 */

public class FilterAdapter extends RecyclerView.Adapter<Filter> {

    private List<String> filterList = new ArrayList<String>();
    private LayoutInflater inflater;


    public FilterAdapter(LayoutInflater l){
        this.inflater = l;
    }

    @Override
    public Filter onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view from filter.xml file
        LinearLayout v = (LinearLayout) inflater.from(parent.getContext()).inflate(R.layout.filter, parent, false);

        Filter f = new Filter(v);
        return f;

    }

    @Override
    public void onBindViewHolder(Filter holder, int position) {

        holder.getTextFilter().setText(filterList.get(position).toString());
        holder.getSwitchFilter().setChecked(false);

    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }

    /**
     * Method responsible for the swapping action when the filter is being dragged
     *
     * @param firstPosition The previous position of the dragged filter item
     * @param secondPosition The next position to drop the filter recently dragged out.
     */
    public void swapListItem(int firstPosition, int secondPosition){
        Collections.swap(filterList, firstPosition, secondPosition);
        notifyItemMoved(firstPosition, secondPosition);
    }

    /**
     *  When the user click 'Get Filters' button, this method is called to populate the RecyclerView
     *  list with the filters downloaded from the server.
     *
     * @param n The Item to be added in the RecyclerView list
     */
    public void addListItem(String n){
        filterList.add(n);
        notifyDataSetChanged();
    }


    /**
     * Whenever the user reload the filter list by click the 'Get Filters' button, this method is
     * invoked to clean up the list first
     */
    public void removeAllListItems() {
        filterList.removeAll(filterList);
        notifyDataSetChanged();
    }

}
