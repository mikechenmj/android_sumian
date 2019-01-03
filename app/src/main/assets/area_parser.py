import json


def parseData(sourceFile, targeFile):
    provinceList = []
    with open(sourceFile) as f:
        data = json.load(f)
        locations = data['RECORDS']
        for p in getSubLocations(locations, '1'):
            province = {'name': p['name'], 'city': []}
            for c in getSubLocations(locations, p['id']):
                city = {'name': c['name'], 'area': []}
                for a in getSubLocations(locations, c['id']):
                    city['area'].append(a['name'])
                province['city'].append(city)
            provinceList.append(province)
        print provinceList
        with open(targeFile, 'w') as outfile:
            json.dump(provinceList, outfile)


def getSubLocations(locations, parent_id):
    subLocations = []
    for l in locations:
        if l['parent_id'] == parent_id:
            subLocations.append(l)
    return subLocations


parseData('areas.json', 'areas3.json')
