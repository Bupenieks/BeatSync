# BeatSync
This is an Andriod app that will synchronize your rowing strokes with songs imported from selected Spotify playlists. To allow for more accurate synchronization, import as many different playlists from Spotify as possible for a wide range of possible song bpm's.

To begin automatic synchronization, enter the rowing program state and begin rowing. The app will alot a fixed time interval to analyzing
your strokes and will begin playing matched songs. Alternatively, manually enter your target stroke rate for more accurate synchronization.

Rowing stroke timing is quantified by reading accelerometer data, applying a low-pass filter and moving average,
and normalizing the resulting acceleration vectors.

Currently the app uses a very naive implementation of tempo detection -- that is to set certain acceleration
threshold markers for peaks and troughs and record appropriate time intervals as they're are crossed. Since rowing strokes roughly follow a sinusoidal movement, it would be plausible to apply a sinusoidal regression to the data and estimate
a frequency, and calculate a target bpm from that.
