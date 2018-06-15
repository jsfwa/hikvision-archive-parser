# hikvision-archive-parser

## Usage

```hInfo = new DefaultArchiveParser("<path to archive dir>") with ArchiveSettings
hInfo.parse()```

## Default Archive Structure

* info.bin - Contains description about directories and index files
	* dataDir\[X\] - Contains index and media files
		* index00.bin - Contains information about actual media files, also contains information about media clips(segments) in this files
		* hiv\[XXXX\].mp4 - __Not a normal video file!__, by the fact contains sets of segments, hence you need to know the exact offset to extract real clip

### Hikvision Info Structure

| serialNumber | macAddress | res | size | blocks | dataDirs |
|--------------|------------|-----|------|--------|----------|
|      48b     |     6b     |  2b |  4b  |   4b   |    4b    |


### Hikvision Index File Structure

| modifyTimes | version | filecount | nextFileRecNo | lastFileRecNo | currFileRecNo | res | checksum |
|-------------|---------|-----------|---------------|---------------|---------------|-----|----------|
|      8b     |    4b   |    4b     |       4b      |       4b      |     1176b     | 76b |    4b    |


### Hikvision File Structure

| fileNo | channel | segRecNums | startTime | endTime | status | res1 | lockedSegNum | res2 | infoTypes |
|--------|---------|------------|-----------|---------|--------|------|--------------|------|-----------|
|   4b   |    2b   |     2b     |     4b    |    4b   |   1b   |  1b  |      2b      |  4b  |     8b    |


### Hikvision Segment Structure

| bType | status | res1 | resolution | startTime | endTime | firstKeyFrameAbsTime | firstKeyFrameStdTime |
|-------|--------|------|------------|-----------|---------|----------------------|----------------------|
|   1b  |   1b   |  2b  |     4b     |     8b    |    8b   |          8b          |           8b         |

| lastKeyFrameStdTime | startOffset | endOffset | res2 | infoNum | infoTypes | infoStartTime | infoEndTime| infoStartOffeset | infoEndOffset |       
|---------------------|-------------|-----------|------|---------|-----------|---------------|------------|------------------|---------------|         
|         8b          |      4b     |     4b    |  4b  |    4b   |    8b     |      4b       |     4b     |        4b        |       4b      |


