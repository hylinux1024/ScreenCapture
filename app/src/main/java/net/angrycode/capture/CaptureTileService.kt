package net.angrycode.capture

import android.annotation.TargetApi
import android.os.Build.VERSION_CODES.N
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import timber.log.Timber

/**
 * Created by pc on 2017/12/31.
 */
@TargetApi(N)  // Only created on N+
class CaptureTileService : TileService() {
    override fun onClick() {
        startActivity(CaptureShortcutLaunchActivity.createQuickTileIntent(this))
    }

    override fun onStartListening() {
        Timber.i("Quick tile started listening")
        val tile = qsTile
        tile.state = Tile.STATE_ACTIVE
        tile.updateTile()
    }

    override fun onStopListening() {
        Timber.i("Quick tile stopped listening")
    }

    override fun onTileAdded() {
        Timber.i("Quick tile added")
//        analytics.send(HitBuilders.EventBuilder() //
//                .setCategory(Analytics.CATEGORY_QUICK_TILE)
//                .setAction(Analytics.ACTION_QUICK_TILE_ADDED)
//                .build())
    }

    override fun onTileRemoved() {
        Timber.i("Quick tile removed")
//        analytics.send(HitBuilders.EventBuilder() //
//                .setCategory(Analytics.CATEGORY_QUICK_TILE)
//                .setAction(Analytics.ACTION_QUICK_TILE_REMOVED)
//                .build())
    }
}