package org.krauss.filtextulator;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.Collections;
import java.util.List;

/**
 * Created by jrkrauss on 24/04/17.
 *
 * Class necessary to implement the drag & drop feature within the RecyclerView list.
 *
 */

public class FilterTouchHelper extends ItemTouchHelper.Callback {

    private FilterAdapter adapter;

    public FilterTouchHelper(FilterAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * Method that swap the filters items when the user is dragging a filter.
     *
     * @param recyclerView The RecycleView list
     * @param viewHolder The filter item selected by the user
     * @param target The next filter position to drop the filter
     * @return true if succeed.
     */
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        adapter.swapListItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) { }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.DOWN | ItemTouchHelper.UP);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

};
