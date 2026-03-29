from flask import Flask, request, jsonify
from opcua import Server
import threading

app = Flask(__name__)

# Setup OPC UA server
server = Server()
server.set_endpoint("opc.tcp://localhost:4840/freeopcua/server/")
idx = server.register_namespace("http://sysarch.org/opcua/server/")
my_obj = server.get_objects_node().add_object(idx, "Elevator")

# Setup variables edited by the HMI

targetLevel = my_obj.add_variable(idx,"targetLevel",-1)
targetLevel.set_writable()

directionRequest = my_obj.add_variable(idx,"directionRequest",0)
directionRequest.set_writable()

doorCommand = my_obj.add_variable(idx,"doorCommand","closed")
doorCommand.set_writable()

reset = my_obj.add_variable(idx,"reset",False)
reset.set_writable()

speedSupervisor = my_obj.add_variable(idx,"speedSupervisor",0)
speedSupervisor.set_writable()

crawlSelect = my_obj.add_variable(idx,"crawlSelect",-10)
crawlSelect.set_writable()

# Setup variables sent back to the HMI

controlAPIlog = my_obj.add_variable(idx, "controlAPIlog", "test log")
elevatorPosition = my_obj.add_variable(idx,"elevatorPosition",0)
doorStatus = my_obj.add_variable(idx,"doorStatus",'open')
motorState = my_obj.add_variable(idx,"motorState",'off')
cabinSensors = my_obj.add_variable(idx,"cabinSensors",[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0])
elevatorPosition.set_writable()
doorStatus.set_writable()
motorState.set_writable()
cabinSensors.set_writable()
controlAPIlog.set_writable()
server.start()

# REST endpoint for Java to call
@app.route('/update', methods=['POST'])
def update_values():
    data = request.json

    #updating variables to avoid duplicate commands

    tl=data.get('targetLevel')
    if tl is not None:
        targetLevel.set_value(int(tl))
    dr=data.get('directionRequest')
    if dr is not None:
        directionRequest.set_value(bool(dr))
    dc=data.get('doorCommand')
    if dc is not None:
        doorCommand.set_value(str(dc))
    res = data.get('reset')
    if res is not None:
        reset.set_value(bool(res))
        print(res)
    sps = data.get('speedSupervisor')
    if sps is not None:
        speedSupervisor.set_value(int(sps))
    cs=data.get('crawlSelect')  
    if cs is not None:
        crawlSelect.set_value(int(cs))

    #updating variables sent back to the HMI

    ep=data.get('elevatorPosition')
    if ep is not None:
        elevatorPosition.set_value(int(ep))
        print(ep)
    ds=data.get('doorStatus')
    if ds is not None:
        doorStatus.set_value(str(ds))
    ms=data.get('motorState')
    if ms is not None:
        motorState.set_value(str(ms))
    cs=data.get('cabinSensors')
    if cs is not None:
        cabinSensors.set_value(list[bool](cs))
    log=data.get('controlAPIlog')
    if log is not None: 
        controlAPIlog.set_value(str(log))
    
    print(reset)
    
    return jsonify({"status": "updated"}), 200


# REST endpoint for Java to GET current values
@app.route('/values', methods=['GET'])
def get_values():
    return jsonify({
        "targetLevel": targetLevel.get_value(),
        "directionRequest": directionRequest.get_value(),
        "doorCommand": doorCommand.get_value(),
        "reset": reset.get_value(),
        "speedSupervisor": speedSupervisor.get_value(),
        "crawlSelect": crawlSelect.get_value(),

    })
# Run Flask in a separate thread
def run_flask():
    app.run(port=5000)

threading.Thread(target=run_flask).start()
print("OPC UA Server and REST API running...")

try:
    while True:
        pass
except KeyboardInterrupt:
    print("Shutting down")
    server.stop()
