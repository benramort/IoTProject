from locust import HttpUser, task

class Performance(HttpUser):
    @task
    def hello_world(self):
        self.client.get("/")

    @task
    def get_sensors(self):
        self.client.get("/sensors")
    
    @task
    def find_mode(self):
        self.client.get("/findMode")

    @task
    def configure_settings(self):
        self.client.request("PUT", "/settings", data=("asdsfasdf").encode(), json='{"auto_light": true,"auto_light_level_on": 200,"auto_light_level_off": 2000,"proximity_lock": false,"proximity_meters": 10}' )

    # @task
    # def set_light(self):
    #     self.client.put("/light", json='{"state": true}' )

    # @task
    # def set_lock(self):
    #     self.client.put("/light", json='{"state": true}' )

    # @task
    # def proximity_check(self):
    #     self.client.put("/proximity", json='{"lat": 65.059950, "lon": 25.466049}')

    # @task
    # def gps_history(self):
    #     self.client.put("/getGps", json='{"start": "010124","end": "010126"}')