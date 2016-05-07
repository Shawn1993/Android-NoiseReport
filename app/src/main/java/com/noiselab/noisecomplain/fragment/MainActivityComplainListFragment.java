package com.noiselab.noisecomplain.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.noiselab.noisecomplain.R;
import com.noiselab.noisecomplain.activity.ComplainActivity;
import com.noiselab.noisecomplain.model.ComplainForm;
import com.noiselab.noisecomplain.model.ComplainFormDao;
import com.noiselab.noisecomplain.utility.AppConfig;
import com.noiselab.noisecomplain.utility.DateUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivityComplainListFragment extends Fragment {

    // the widgets
    private FloatingActionButton mGotoComplainButton;
    private RecyclerView mRecyclerView;
    private List<ComplainForm> mComplainForms;
    private ListAdapter mAdapter;

    private void initData() {
        ComplainFormDao dao = new ComplainFormDao(getActivity());
        mComplainForms = dao.queryAll();
        Collections.reverse(mComplainForms);
    }

    private void initGotoComplainButton(View view) {
        mGotoComplainButton = (FloatingActionButton) view.findViewById(R.id.complain_fab);
        mGotoComplainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ComplainActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void initRecyclerView(final View view) {
        mAdapter = new ListAdapter();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_fragment_complain_list);
        }

        View view = inflater.inflate(R.layout.fragment_complain_list, container, false);
        initGotoComplainButton(view);
        initRecyclerView(view);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String id = data.getStringExtra("formId");
            ComplainFormDao dao = new ComplainFormDao(getActivity());
            ComplainForm form = dao.queryById(id);
            mComplainForms.add(0, form);
            mAdapter.notifyDataSetChanged();
        }
    }


    class ListAdapter extends RecyclerView.Adapter<ListAdapter.Holder> {

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.complain_list_item, parent, false);
            ListAdapter.Holder holder = new ListAdapter.Holder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            String title = (int) (mComplainForms.get(position).averageIntensity) + "db";
            String subTitle = mComplainForms.get(position).manualAddress;
            if (subTitle == null) {
                subTitle = mComplainForms.get(position).autoAddress;
            }

            String dateStr = mComplainForms.get(position).date;
            Date date = DateUtil.toDate(AppConfig.DATE_FORMAT, dateStr);
            SimpleDateFormat formatter = new SimpleDateFormat();
            if (DateUtil.isToday(date)) {
                formatter.applyPattern("HH:mm");
            } else {
                formatter.applyPattern("M月d日");
            }

            holder.titleText.setText(title);
            holder.subTitleText.setText(subTitle);
            holder.dateText.setText(formatter.format(date));
        }

        @Override
        public int getItemCount() {
            if (mComplainForms != null) {
                return mComplainForms.size();
            }
            return 0;
        }

        class Holder extends RecyclerView.ViewHolder {

            TextView titleText;
            TextView subTitleText;
            TextView dateText;
            ImageView imageView;

            public Holder(View itemView) {
                super(itemView);
                titleText = (TextView) itemView.findViewById(R.id.title);
                subTitleText = (TextView) itemView.findViewById(R.id.subtitle);
                dateText = (TextView) itemView.findViewById(R.id.date);
                imageView = (ImageView) itemView.findViewById(R.id.image);
            }
        }
    }

    static class DividerItemDecoration extends RecyclerView.ItemDecoration {


        private static final int[] ATTRS = new int[]{
                android.R.attr.listDivider
        };

        private Drawable mDivider;

        public DividerItemDecoration(Context context) {
            final TypedArray a = context.obtainStyledAttributes(ATTRS);
            mDivider = a.getDrawable(0);
            a.recycle();
        }


        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDraw(c, parent, state);

            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }

        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 1);
        }
    }


}
