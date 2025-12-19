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