package org.krauss.filtextulator;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by jrkrauss on 26/03/17.
 *
 * Class that represents a filter.
 *
 * A Filter extends ViewHolder, so each filter can be easily added to the RecyclerView list.
 * The Filter class also holds a LinearLayout which is nothing but the carrying container for the
 * three components that each filter has.
 *
 */
public class Filter extends RecyclerView.ViewHolder {

    // each data item is just a string in this case
    public LinearLayout filterItem;

    public Filter(LinearLayout v) {
        super(v);
        filterItem = v;
    }


    /**
     *
     * @return The component TextView from the '@layout:filter.xml' file
     */
    public TextView getTextFilter(){
        return (TextView) this.filterItem.getChildAt(1);
    }

    /**
     *
     * @return The component Switch from the '@layout:filter.xml' file
     */
    public Switch getSwitchFilter(){
        return (Switch) this.filterItem.getChildAt(2);
    }


}
