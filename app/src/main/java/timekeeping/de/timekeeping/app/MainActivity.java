package timekeeping.de.timekeeping.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.chopping.utils.DeviceUtils;

import timekeeping.de.timekeeping.R;
import timekeeping.de.timekeeping.adapters.ItemsGridViewListAdapter;
import timekeeping.de.timekeeping.data.Time;

/**
 * The {@link timekeeping.de.timekeeping.app.MainActivity}.
 *
 * @author Xinyue Zhao
 */
public class MainActivity extends ActionBarActivity implements OnInitListener {
	/**
	 * Speak text.
	 */
	private TextToSpeech mTextToSpeech;
	/**
	 * Holding all saved {@link timekeeping.de.timekeeping.data.Time}s.
	 */
	private GridView mGridView;
	/**
	 * {@link android.widget.Adapter} for {@link #mGridView}.
	 */
	private ItemsGridViewListAdapter mAdp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Let all columns to equal.
		mGridView = (GridView) findViewById(R.id.schedule_gv);
		int screenWidth = DeviceUtils.getScreenSize(this, 0).Width;
		mGridView.setColumnWidth(screenWidth / 3);

		//Init speech-framework.
		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, 0x1);

		mAdp = new ItemsGridViewListAdapter();
		List<Time> times = new ArrayList<Time>();
		times.add(new Time(1, 2, 3, System.currentTimeMillis(), false));
		times.add(new Time(2, 21, 13, System.currentTimeMillis(), true));
		times.add(new Time(3, 22, 23, System.currentTimeMillis(), false));
		times.add(new Time(4, 23, 43, System.currentTimeMillis(), true));
		times.add(new Time(5, 11, 43, System.currentTimeMillis(), true));
		times.add(new Time(5, 20, 23, System.currentTimeMillis(), false));
		mAdp.setItemList(times);
		mGridView.setAdapter(mAdp);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0x1) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				mTextToSpeech = new TextToSpeech(getApplicationContext(), this);
				Toast.makeText(this, mTextToSpeech.isLanguageAvailable(Locale.GERMAN) + "", Toast.LENGTH_SHORT).show();
				mTextToSpeech.setLanguage(Locale.GERMANY);
			} else {
				// missing data, install it
				Intent installIntent = new Intent();
				installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void sayHello(View view) {
		String myText1 = "12:23";
		mTextToSpeech.speak(myText1, TextToSpeech.QUEUE_FLUSH, null);
	}

	@Override
	public void onInit(int status) {
	}

}
