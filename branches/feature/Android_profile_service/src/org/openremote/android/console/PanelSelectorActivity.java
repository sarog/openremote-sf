package org.openremote.android.console;

import java.util.List;

import org.openremote.android.console.model.AppSettingsModel;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PanelSelectorActivity extends ListActivity {

   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       List<String> panelList = HTTPUtil.getPanels(getIntent().getDataString());
       setListAdapter(new ArrayAdapter<String>(this,
               android.R.layout.simple_list_item_single_choice, panelList));
       
       final ListView listView = getListView();

       listView.setItemsCanFocus(false);
       listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String selectedPanel = (String)parent.getItemAtPosition(position);
            Intent intent = getIntent();
            intent.setData(Uri.parse(selectedPanel));
            setResult(Constants.RESULT_PANEL_SELECTED, intent);
            AppSettingsModel.setCurrentPanelIdentity(PanelSelectorActivity.this, selectedPanel);
            finish();
         }
       });
       String panel = AppSettingsModel.getCurrentPanelIdentity(PanelSelectorActivity.this);
       if (!TextUtils.isEmpty(panel)) {
          int panelCount = panelList.size();
          for (int i = 0; i < panelCount; i++) {
            if (panel.equals(panelList.get(i))) {
               listView.setItemChecked(i, true);
               break;
            }
         }
       }
   }

}
