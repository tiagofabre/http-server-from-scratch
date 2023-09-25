(require '[clojure.java.io :as io])
(import '[java.net ServerSocket])

(defn receive
  "Read a line of textual data from the given socket"
[socket]
  (.readLine (io/reader socket)))

(defn send
  "Send the given string message out over the given socket"
  [socket msg]
  (let [writer (io/writer socket)]
      (.write writer msg)
      (.flush writer)))

(defn serve [port handler]
  (println "started!")
  (with-open [server-sock (ServerSocket. port)
              sock (.accept server-sock)]
    (let [msg-in (receive sock)
          msg-out (handler msg-in)]
      (send sock msg-out))))

(defn serve-persistent [port handler]
  (let [running (atom true)]
    (future
      (with-open [server-sock (ServerSocket. port)]
        (while @running
          (with-open [sock (.accept server-sock)]
            (let [msg-in (receive sock)
                  msg-out (handler msg-in)]
              (send sock msg-out))))))
    running))

(defn hello-world [request] 
         (println request)
         "HTTP/1.1 200 OK\nConnection: close\n\nHello, socket!")

(def port 9898)

;; (serve port hello-world)

(serve-persistent port hello-world)
