import os

ffmpeg_path = "C:/ffmpeg/ffmpeg.exe"

os.environ["IMAGEIO_FFMPEG_EXE"] = ffmpeg_path
from Katna.video import Video

def main():

       vd = Video()

       output_folder_for_compressed_videos= "compressed_folder"
       out_dir_path = os.path.join(".", output_folder_for_compressed_videos)

       if not os.path.isdir(out_dir_path):
            os.mkdir(out_dir_path)

       video_file_path = os.path.join(".", "test.mp4")
       print(f"Input video file path = {video_file_path}")

       status = vd.compress_video(
            file_path=video_file_path,
            out_dir_path=out_dir_path,
            crf_parameter = 40
       )


if __name__ == "__main__":
       main()