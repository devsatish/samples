/*
This example shows how to use React360 library and extend it to
generate dynamic 360 surfaces and render them in 3D Space.

Draft - early version to enable dynamic surface generation
Refer to HotspotCalculator.ts for converting X,Y from 3D Image jpg to 3D space co-ords

Tags: TypeScript, React 360

*/

import {ReactInstance, Surface} from 'react-360-web';
import {Module} from 'react-360-web';

var dataset = {
    "startImage": "data-viz-view-start.jpg",
    "hotspots": [
        {
            "from": {
                "cameraId": "CAM1",
                "fileName": "360-view-1.jpg",
                "cameraLabel": "View1"
            },
            "to": {
                "cameraId": "CAM2",
                "fileName": "360-view-2.jpg",
                "cameraLabel": "View2"
            },
            "horizontalAngle": 1.2998066042062808,
            "verticalAngle": 2.5292835676012824
        },
        ///redacted rest of cameras
    ],
    "nSurfaces": 4
};
var surfaceArray = [];
var mysurface;
var uniqueCams;
var hotspots
var nSurfaces;
var r360;

class SurfaceModule extends Module {
    constructor() {
        console.log('constructor - SurfaceModule');
        super('SurfaceModule');
    }
    trigger(surfaceName, horizontalAngle, verticalAngle) {
        console.log('Trigger to open cam  ' + surfaceName);
        // CAM2 rendered.
       // Get CAM2 Hotspots and change surfaceArray to those hotspots;
       
        var clickedAtCamHotSpots = [];

        for (i = 0; i < hotspots.length; i ++) {
        hs = hotspots[i];
        if (hs.from.cameraId == surfaceName) {
            clickedAtCamHotSpots.push(hs);
            }
        }
        console.log(clickedAtCamHotSpots);
     
        for (i = 0; i < nSurfaces - 1; i ++) {

        s = surfaceArray[i] ;
        s.resize(256, 256);
        s.setAngle(clickedAtCamHotSpots[i].to.horizontalAngle, clickedAtCamHotSpots[i].to.verticalAngle);
        r360.renderToSurface(r360.createRoot('MultiPanel', {
            lbl: clickedAtCamHotSpots[i].to.cameraLabel,
            fn: clickedAtCamHotSpots[i].to.fileName,
            to: clickedAtCamHotSpots[i].to.cameraId
        }), s);
        }







    }
};
function removeDuplicates(myArr, prop) {
    return myArr.filter((obj, pos, arr) => {
        return arr.map(mapObj => mapObj[prop]).indexOf(obj[prop]) === pos;
    });
}
function init(bundle, parent, options =
    {}) {
    r360 = new ReactInstance(bundle, parent, {
        fullScreen: true,
        nativeModules: [new SurfaceModule()]
    });
    nSurfaces = dataset.nSurfaces;
    cameras = dataset.cameras;
    hotspots = dataset.hotspots;
    // initialize - first loop
    // Get first Camera and its hotspots
    const cams = [];
    for (i = 0; i < hotspots.length; i ++) {
        hs = hotspots[i];
        t = hs.to;
        f = hs.from;
        cams.push(f);
        cams.push(t);
    }
    // /const uniqueCams = [...(new Set(cams.map(({ cameraId }) => cameraId)))]
    uniqueCams = removeDuplicates(cams, "cameraId");
    uniqueCams.sort();
    firstCam = uniqueCams[0];
    firstCamHotspots = [];
    for (i = 0; i < hotspots.length; i ++) {
        hs = hotspots[i];
        if (hs.from.cameraId == firstCam.cameraId) {
            firstCamHotspots.push(hs);
        }
    }
    console.log(firstCamHotspots);
    for (i = 0; i < nSurfaces - 1; i ++) {
        s = new Surface(256, 256, Surface.SurfaceShape.Flat);
        s.resize(256, 256);
        if (i != 0) {
            s.setAngle(firstCamHotspots[i].horizontalAngle, firstCamHotspots[i].verticalAngle)
        }
        surfaceArray[i] = s;
    }
    mysurface = new Surface(256, 256, Surface.SurfaceShape.Flat);
    mysurface.resize(256, 256);
    surfaceArray[0] = r360.getDefaultSurface();
    for (i = 0; i < nSurfaces - 1; i ++) {
        r360.renderToSurface(r360.createRoot('MultiPanel', {
            lbl: firstCamHotspots[i].to.cameraLabel,
            fn: firstCamHotspots[i].to.fileName,
            to: firstCamHotspots[i].to.cameraId
        }), surfaceArray[i]);
    }
    r360.compositor.setBackground(r360.getAssetURL(dataset.startImage));
}
window.React360 = {
    init
};