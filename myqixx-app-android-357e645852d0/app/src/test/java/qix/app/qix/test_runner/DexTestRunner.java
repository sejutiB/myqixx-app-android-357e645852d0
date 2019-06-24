package qix.app.qix.test_runner;

import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.test.runner.AndroidJUnitRunner;

import androidx.multidex.MultiDex;
import androidx.test.runner.AndroidJUnitRunner;

import static androidx.test.InstrumentationRegistry.getTargetContext;

public class DexTestRunner extends AndroidJUnitRunner {

        @Override
        public void onCreate(Bundle arguments)
        {
            MultiDex.install(getTargetContext());
            super.onCreate(arguments);
        }
}
