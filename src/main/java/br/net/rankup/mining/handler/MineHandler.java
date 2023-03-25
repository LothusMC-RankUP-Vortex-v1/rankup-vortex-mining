package br.net.rankup.mining.handler;

import java.io.*;

import br.net.rankup.mining.adapter.CuboidAdapter;
import br.net.rankup.mining.adapter.LocationAdapter;
import br.net.rankup.mining.model.mine.MineModel;
import br.net.rankup.mining.utils.AbstractHandler;
import br.net.rankup.mining.utils.Cuboid;
import org.bukkit.*;
import com.google.common.collect.*;
import java.util.*;

public class MineHandler extends AbstractHandler<String, MineModel>
{
    public MineHandler(final File file, final String path) {
        super(file, path, (Map)ImmutableMap.of((Object) Cuboid.class, (Object)new CuboidAdapter(), (Object)Location.class, (Object)new LocationAdapter()));
    }
    
    @Override
    public Class<MineModel> getType() {
        return MineModel.class;
    }
    
    @Override
    public Class<MineModel[]> getArrayType() {
        return MineModel[].class;
    }
}
