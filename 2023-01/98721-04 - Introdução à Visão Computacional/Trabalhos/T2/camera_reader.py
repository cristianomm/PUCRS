
import cv2 as cv


path = 'J:/Disco/Camera/192.168.0.211_80/2023/06/25/'
files = ['rec_2023_06_25_12_14_29.mp4', 'rec_2023_06_25_12_14_48.mp4', 'rec_2023_06_25_12_15_20.mp4']

capture = cv.VideoCapture(path + files[0])

# Get the Default resolutions
frame_width = int(capture.get(3))
frame_height = int(capture.get(4))
print(frame_width, frame_height)


#show video
while True:
    ret, frame = capture.read()
    print(ret)
    print(frame)
    cv.imshow('frame', frame)
    if cv.waitKey(27):
        break

capture.release()
cv.destroyAllWindows()
