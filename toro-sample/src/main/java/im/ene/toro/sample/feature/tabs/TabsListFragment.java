/*
 * Copyright 2016 eneim@Eneim Labs, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.ene.toro.sample.feature.tabs;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import im.ene.toro.MediaPlayerManager;
import im.ene.toro.Toro;
import im.ene.toro.ToroScrollListener;
import im.ene.toro.sample.BaseToroFragment;
import im.ene.toro.sample.R;
import im.ene.toro.sample.feature.legacy.LegacyActivity;
import im.ene.toro.sample.widget.DividerItemDecoration;

/**
 * Created by eneim on 6/30/16.
 */
public class TabsListFragment extends BaseToroFragment {

  protected RecyclerView recyclerView;
  protected Tabs1Adapter adapter;

  public static TabsListFragment newInstance() {
    return new TabsListFragment();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.generic_recycler_view, container, false);
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2) @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    RecyclerView.LayoutManager layoutManager = getLayoutManager();
    recyclerView.setLayoutManager(layoutManager);
    if (layoutManager instanceof LinearLayoutManager) {
      recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
          ((LinearLayoutManager) layoutManager).getOrientation()));
    }

    adapter = new Tabs1Adapter();
    adapter.setOnItemClickListener(new Tabs1Adapter.ItemClickListener() {
      @Override public void onItemClick(RecyclerView.ViewHolder viewHolder, View view) {
        super.onItemClick(viewHolder, view);
        Intent intent = new Intent(getContext(), LegacyActivity.class);
        startActivity(intent);
      }
    });
    recyclerView.setHasFixedSize(false);
    recyclerView.setAdapter(adapter);
  }

  /**
   * called when this fragment (tab) becomes active or inactive
   */
  public void notifyTabSelected(boolean isSelected) {
    final ToroScrollListener scrollListener = Toro.getScrollListener(recyclerView);
    if (scrollListener == null) {
      // should never happen as long as Toro.register() was called
      return;
    }
    MediaPlayerManager manager = scrollListener.getManager();
    if (isSelected && manager.getPlayer() == null) {
      // NOTE: first time a tab/fragment is selected it doesn't start to playback until user starts to scroll
      //   > if we can just simulate a scroll event to ToroScrollListener once tab is visible this issue is fixed
      new Handler().postDelayed(new Runnable() {
        @Override public void run() {
          scrollListener.onScrollStateChanged(recyclerView, RecyclerView.SCROLL_STATE_IDLE);
        }
      }, 100);
    } else if (isSelected) {
      // resume video playback
      manager.startPlayback();
    } else {
      // pause video playback
      manager.pausePlayback();
    }
  }

  @Override protected void dispatchFragmentActivated() {
    Toro.register(recyclerView);
  }

  @Override protected void dispatchFragmentDeActivated() {
    Toro.unregister(recyclerView);
  }

  RecyclerView.LayoutManager getLayoutManager() {
    return new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
  }
}
